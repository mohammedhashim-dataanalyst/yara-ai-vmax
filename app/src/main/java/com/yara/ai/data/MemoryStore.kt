package com.yara.ai.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

/**
 * Local-brain storage. Everything Yara knows lives on-device:
 *  - chat history        (chat_history.json)
 *  - unlimited memories  (memories.json)
 *  - learned Q->A pairs  (learned.json)   <- offline fallback brain
 *  - feature toggles     (features.json)
 *  - api providers       (providers.json)
 *  - account             (account.json)
 *
 * Works without network — Yara keeps replying from this local brain
 * when every API key is exhausted.
 */
class MemoryStore private constructor(ctx: Context) {

    private val gson = Gson()
    private val root = File(ctx.filesDir, "yara_brain").apply { mkdirs() }
    private val mutex = Mutex()

    private val historyFile = File(root, "chat_history.json")
    private val memoriesFile = File(root, "memories.json")
    private val learnedFile = File(root, "learned.json")
    private val featuresFile = File(root, "features.json")
    private val providersFile = File(root, "providers.json")
    private val accountFile = File(root, "account.json")

    @Volatile var history: MutableList<ChatMessage> = mutableListOf()
        private set
    @Volatile var memories: MutableList<Memory> = mutableListOf()
        private set
    /** learned phrase -> best reply (offline brain) */
    @Volatile var learned: MutableMap<String, String> = mutableMapOf()
        private set
    /** feature key -> enabled */
    @Volatile var features: MutableMap<String, Boolean> = mutableMapOf()
        private set
    @Volatile var providers: MutableList<ProviderState> = mutableListOf()
        private set
    @Volatile var account: Account = Account()
        private set
    /** trait key -> TraitSetting */
    @Volatile var traits: MutableMap<String, TraitSetting> = mutableMapOf()
        private set

    suspend fun load() = withContext(Dispatchers.IO) {
        mutex.withLock {
            history = readList(historyFile)
            memories = readList(memoriesFile)
            learned = readMap(learnedFile)
            features = readMap(featuresFile)
            providers = readList(providersFile)
            account = readObj(accountFile, Account::class.java) ?: Account()
            traits = readMap(traitsFile())
            if (providers.isEmpty()) seedProviders()
        }
    }

    private fun traitsFile(): File = File(root, "traits.json")

    private fun seedProviders() {
        providers = mutableListOf(
            ProviderState("Gemini"),
            ProviderState("Grok"),
            ProviderState("OpenRouter"),
            ProviderState("HuggingFace")
        )
        saveProvidersNow()
    }

    // ---------- chat ----------
    suspend fun addMessage(m: ChatMessage) = withContext(Dispatchers.IO) {
        mutex.withLock {
            history.add(m)
            // keep most recent 2000 for speed; full export lives in exports/
            if (history.size > 2000) history = history.takeLast(2000).toMutableList()
            writeList(historyFile, history)
        }
    }

    suspend fun updateMessage(m: ChatMessage) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val i = history.indexOfFirst { it.id == m.id }
            if (i >= 0) { history[i] = m; writeList(historyFile, history) }
        }
    }

    suspend fun clearChat() = withContext(Dispatchers.IO) {
        mutex.withLock { history.clear(); writeList(historyFile, history) }
    }

    // ---------- memories (unlimited) ----------
    suspend fun addMemory(text: String, importance: Int = 5, tags: List<String> = emptyList(), source: String = "user") =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                memories.add(Memory(text = text, tags = tags, importance = importance, source = source))
                writeList(memoriesFile, memories)
            }
        }

    suspend fun deleteMemory(id: String) = withContext(Dispatchers.IO) {
        mutex.withLock { memories.removeAll { it.id == id }; writeList(memoriesFile, memories) }
    }

    /** Rank memories for recall: importance + recency + keyword overlap. */
    fun recallMemories(query: String, limit: Int = 8): List<Memory> {
        val q = query.lowercase()
        val now = System.currentTimeMillis()
        return memories
            .map { m ->
                var score = m.importance.toDouble()
                score += if (q.isNotBlank() && q.split(" ").any { m.text.lowercase().contains(it) }) 4.0 else 0.0
                score += (1.0 - ((now - m.createdAt).coerceAtLeast(0) / 86_400_000.0 / 30)).coerceIn(0.0, 1.0)
                m.lastRecalledAt = now
                m to score
            }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }

    // ---------- offline learned brain ----------
    suspend fun teach(prompt: String, reply: String) = withContext(Dispatchers.IO) {
        mutex.withLock {
            learned[prompt.lowercase().take(160)] = reply
            if (learned.size > 5000) {
                // drop oldest arbitrary entries when huge
                val keep = learned.entries.takeLast(4000)
                learned = keep.associate { it.key to it.value }.toMutableMap()
            }
            writeMap(learnedFile, learned)
        }
    }

    /** Find a previously seen similar prompt -> offline reply. */
    fun offlineReply(prompt: String): String? {
        val p = prompt.lowercase()
        learned[p]?.let { return it }
        // fuzzy: share at least 60% of words
        val words = p.split(Regex("\\s+")).filter { it.length > 2 }.toSet()
        var best: String? = null
        var bestScore = 0.0
        for ((k, v) in learned) {
            val kw = k.split(Regex("\\s+")).filter { it.length > 2 }.toSet()
            if (kw.isEmpty()) continue
            val inter = words.intersect(kw).size.toDouble()
            val score = inter / (words.size + kw.size - inter)
            if (score > bestScore && score > 0.6) { bestScore = score; best = v }
        }
        return best
    }

    // ---------- features ----------
    suspend fun setFeature(key: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        mutex.withLock { features[key] = enabled; writeMap(featuresFile, features) }
    }
    fun isFeatureOn(key: String, def: Boolean = false): Boolean = features[key] ?: def

    // ---------- traits ----------
    suspend fun setTrait(t: TraitSetting) = withContext(Dispatchers.IO) {
        mutex.withLock { traits[t.key] = t; writeMap(traitsFile(), traits) }
    }

    // ---------- providers ----------
    suspend fun saveProviders() = withContext(Dispatchers.IO) {
        mutex.withLock { writeList(providersFile, providers) }
    }
    private fun saveProvidersNow() { writeList(providersFile, providers) }

    // ---------- account ----------
    suspend fun saveAccount(a: Account) = withContext(Dispatchers.IO) {
        mutex.withLock { account = a; writeObj(accountFile, a) }
    }

    // ---------- export / backup ----------
    suspend fun exportAll(): File = withContext(Dispatchers.IO) {
        val out = File(root, "exports").apply { mkdirs() }
        val f = File(out, "yara_backup_${System.currentTimeMillis()}.json")
        val obj = JSONObject()
        obj.put("history", gson.toJsonTree(history))
        obj.put("memories", gson.toJsonTree(memories))
        obj.put("features", gson.toJsonTree(features))
        obj.put("traits", gson.toJsonTree(traits))
        obj.put("account", gson.toJsonTree(account))
        obj.put("exportedAt", System.currentTimeMillis())
        f.writeText(obj.toString(2))
        f
    }

    // ---------- IO helpers ----------
    private fun <T> readList(f: File): MutableList<T> {
        if (!f.exists()) return mutableListOf()
        return try {
            val type = object : TypeToken<MutableList<T>>() {}.type
            gson.fromJson(f.readText(), type) ?: mutableListOf()
        } catch (e: Exception) { mutableListOf() }
    }

    private fun <T> readMap(f: File): MutableMap<String, T> {
        if (!f.exists()) return mutableMapOf()
        return try {
            val type = object : TypeToken<MutableMap<String, T>>() {}.type
            gson.fromJson(f.readText(), type) ?: mutableMapOf()
        } catch (e: Exception) { mutableMapOf() }
    }

    private fun <T> readObj(f: File, cls: Class<T>): T? =
        try { if (f.exists()) gson.fromJson(f.readText(), cls) else null } catch (e: Exception) { null }

    private fun <T> writeList(f: File, v: List<T>) { f.writeText(gson.toJson(v)) }
    private fun <K, V> writeMap(f: File, v: Map<K, V>) { f.writeText(gson.toJson(v)) }
    private fun <T> writeObj(f: File, v: T) { f.writeText(gson.toJson(v)) }

    companion object {
        @Volatile private var I: MemoryStore? = null
        fun get(ctx: Context): MemoryStore =
            I ?: synchronized(this) { I ?: MemoryStore(ctx.applicationContext).also { I = it } }
    }
}
