package com.yara.ai.data

import java.util.UUID

/** One message in the Yara chat thread. */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val fromYara: Boolean,
    val kind: Kind = Kind.TEXT,
    val text: String = "",
    val mediaPath: String? = null,       // image / voice note / doc path
    val emotion: String = "neutral",
    val provider: String = "local",
    val edited: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class Kind { TEXT, IMAGE, VOICE, DOC, STICKER, GIF, POLL, LOCATION, EVENT }
}

/** A long-term memory — unlimited, ranked by importance, recalled into prompts. */
data class Memory(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val tags: List<String> = emptyList(),
    val importance: Int = 5,             // 1..10, drives recall ranking
    val source: String = "chat",         // chat | user | web | feature
    val createdAt: Long = System.currentTimeMillis(),
    var lastRecalledAt: Long = 0L
)

/** Personality / characteristic slider state (1..10 or AUTO). */
data class TraitSetting(
    val key: String,
    val title: String,
    val description: String,
    val isAuto: Boolean = true,
    val level: Int = 6                   // used when not auto
)

/** Per-provider runtime state for load balancing. */
data class ProviderState(
    val name: String,
    var apiKey: String = "",
    var enabled: Boolean = false,
    var lastLatencyMs: Long = Long.MAX_VALUE,
    var failures: Int = 0,
    var lastUsedAt: Long = 0L,
    var rateLimitedUntil: Long = 0L
)

/** User profile / account for backup. */
data class Account(
    var email: String = "",
    var displayName: String = "",
    var backupEnabled: Boolean = false
)
