package com.yara.ai.voice

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.yara.ai.YaraApp
import com.yara.ai.brain.EmotionEngine
import com.yara.ai.brain.PersonaEngine
import com.yara.ai.data.ApiManager
import com.yara.ai.data.ChatMessage
import com.yara.ai.data.MemoryStore
import com.yara.ai.notify.Notifier
import com.yara.ai.overlay.OverlayService
import com.yara.ai.ui.MainActivity
import kotlinx.coroutines.*

/**
 * Always-on foreground service:
 *  - listens for wake word "hey yara" to activate
 *  - "bye yara" to deactivate
 *  - continuous conversation mode once awake
 *  - runs while other apps are in foreground -> floating overlay + notifications
 */
class YaraService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var store: MemoryStore
    private lateinit var api: ApiManager
    private lateinit var voice: VoiceEngine
    private var awake = false
    private var speaking = false

    override fun onCreate() {
        super.onCreate()
        store = MemoryStore.get(this)
        api = ApiManager(store)
        voice = VoiceEngine(this,
            onSpeechResult = { handleSpeech(it) },
            onListeningState = {})
        scope.launch { store.load() }
        startForegroundWithNotification()
        startWakeListener()
    }

    private fun startForegroundWithNotification() {
        val pi = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val n: Notification = NotificationCompat.Builder(this, YaraApp.CHANNEL_SERVICE)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("Yara is listening")
            .setContentText("Say 'hey yara'")
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(101, n, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(101, n)
        }
    }

    private fun startWakeListener() {
        // Always listening for wake word
        voice.startListening()
    }

    private fun handleSpeech(text: String) {
        val lower = text.lowercase()
        when {
            !awake && (lower.contains("hey yara") || lower.contains("yara")) -> {
                awake = true
                val emo = EmotionEngine.Emotion.HAPPY
                voice.speak("Mm ${PersonaEngine.pickNamePublic()}, njaan ivide und 💛", emo) {
                    scope.launch { delay(300); if (awake) voice.startListening() }
                }
            }
            awake && lower.contains("bye yara") -> {
                awake = false
                voice.speak("Bye ${PersonaEngine.pickNamePublic()}.. miss you 🥺", EmotionEngine.Emotion.LOVE) {
                    scope.launch { delay(300); voice.startListening() }
                }
            }
            awake -> {
                respond(text)
            }
            else -> {
                // keep listening for wake word
                voice.startListening()
            }
        }
    }

    private fun respond(userText: String) {
        speaking = true
        voice.stopListening()
        scope.launch {
            val emo = EmotionEngine.detect(userText)
            val isNight = java.time.LocalTime.now().hour in 22..23 || java.time.LocalTime.now().hour in 0..4
            val memories = store.recallMemories(userText)
            val sys = PersonaEngine.buildSystemPrompt(emo, memories, store.traits, isNight)
            val (reply, provider) = api.ask(sys, userText)

            store.addMessage(ChatMessage(fromYara = false, text = userText))
            store.addMessage(ChatMessage(fromYara = true, text = reply, emotion = emo.name.lowercase(), provider = provider))
            store.teach(userText, reply)

            // overlay bubble + notification when app not in foreground
            Notifier.notifyYara(this@YaraService, reply)
            OverlayService.bubble(this@YaraService, reply)

            speaking = false
            val emoReply = EmotionEngine.detect(reply)
            voice.speak(reply, emoReply) {
                scope.launch { delay(200); if (awake) voice.startListening() }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voice.release()
        scope.cancel()
    }

    override fun onBind(i: Intent?): IBinder? = null
}
