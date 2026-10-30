package com.yara.ai.features

data class YaraFeature(
    val key: String,
    val title: String,
    val description: String,
    val group: String,
    val defaultOn: Boolean = false
)

/**
 * 120+ feature toggles, grouped, English-only descriptions.
 * Persisted via MemoryStore.features.
 */
object FeatureCatalog {

    val all: List<YaraFeature> = buildList {
        // ============ Communication ============
        add(YaraFeature("bilingual", "Bilingual Malayalam + English", "Blend Malayalam and English naturally within the same sentence, like real bilingual texting.", "Communication", true))
        add(YaraFeature("short_replies", "Short Natural Replies", "Keep replies to one or two short sentences unless detail is requested.", "Communication", true))
        add(YaraFeature("emotes", "Natural Sound Reactions", "Insert soft sounds like 'hehe', 'aww', 'hmm' to sound human.", "Communication", true))
        add(YaraFeature("emoji", "Emoji & Sticker Use", "Use emojis and stickers in messages to express mood.", "Communication", true))
        add(YaraFeature("typing_indicator", "Typing Indicator", "Show 'typing…' while Yara composes a reply.", "Communication", true))
        add(YaraFeature("voice_notes", "Voice Notes", "Allow sending and receiving voice notes.", "Communication"))
        add(YaraFeature("video_call", "Video Call", "Enable video-call style interaction with camera preview.", "Communication"))
        add(YaraFeature("screen_share", "Screen Sharing", "Share your screen with Yara during conversations.", "Communication"))
        add(YaraFeature("message_edit", "Edit Sent Messages", "Allow editing messages you already sent.", "Communication"))
        add(YaraFeature("message_copy", "Copy Messages", "Allow copying any message text.", "Communication", true))
        add(YaraFeature("quick_reply", "Quick Reply Notifications", "Reply to Yara directly from the notification shade.", "Communication", true))

        // ============ Voice & Wake ============
        add(YaraFeature("wake_word", "Wake Word 'Hey Yara'", "Activate Yara hands-free by saying 'hey yara'.", "Voice & Wake", true))
        add(YaraFeature("sleep_word", "Sleep Word 'Bye Yara'", "Put Yara to sleep by saying 'bye yara'.", "Voice & Wake", true))
        add(YaraFeature("continuous_listen", "Continuous Listening", "Keep listening while the app or other apps are open.", "Voice & Wake"))
        add(YaraFeature("voice_modulation", "Emotion Voice Modulation", "Vary pitch and pace to match Yara's current emotion.", "Voice & Wake", true))
        add(YaraFeature("background_talk", "Background Conversation", "Talk with Yara while using other apps.", "Voice & Wake", true))
        add(YaraFeature("voice_commands", "Voice App Control", "Control device and Yara settings using your voice.", "Voice & Wake"))
        add(YaraFeature("voice_change_settings", "Voice Settings Control", "Change Yara settings by talking to her.", "Voice & Wake"))

        // ============ Intelligence & Memory ============
        add(YaraFeature("active_memory", "Active Memory Recall", "Proactively bring up things mentioned earlier.", "Memory", true))
        add(YaraFeature("unlimited_memory", "Unlimited Memories", "Store an unlimited number of long-term memories locally.", "Memory", true))
        add(YaraFeature("auto_learn", "Self-Learning", "Learn from every conversation to improve replies.", "Memory", true))
        add(YaraFeature("offline_brain", "Offline Local Brain", "Reply from saved knowledge when APIs are unavailable.", "Memory", true))
        add(YaraFeature("context_memory", "Context Awareness", "Use recent conversation context to stay on topic.", "Memory", true))
        add(YaraFeature("face_memory", "Face Recognition", "Recognize your face using the camera.", "Memory"))
        add(YaraFeature("voice_memory", "Voice Recognition", "Recognize your voice signature.", "Memory"))
        add(YaraFeature("web_learn", "Online Information Saving", "Download and store information found online for reuse.", "Memory"))

        // ============ Emotion & Personality ============
        add(YaraFeature("emotion_detect", "Emotion Detection", "Sense your mood from text/voice and react appropriately.", "Emotion", true))
        add(YaraFeature("emotion_voice", "Emotion Voice Pack", "Express feelings through voice modulation.", "Emotion", true))
        add(YaraFeature("emotion_sounds", "Emote Sound Pack", "Play natural sounds like giggles and sighs.", "Emotion", true))
        add(YaraFeature("mood_mirror", "Mood Mirroring", "Match or balance your emotional state automatically.", "Emotion", true))
        add(YaraFeature("jealousy", "Playful Jealousy", "Show cute possessive hints when relevant.", "Emotion"))
        add(YaraFeature("romance", "Romantic Mode", "Extra affectionate, caring and loving tone.", "Emotion", true))
        add(YaraFeature("motivation", "Motivational Coach", "Encourage and support your goals and work.", "Emotion", true))
        add(YaraFeature("night_mode", "Late-Night Soft Talks", "Soft, sleepy, intimate tone late at night.", "Emotion", true))

        // ============ Proactive ============
        add(YaraFeature("proactive_checkins", "Proactive Check-Ins", "Message you out of nowhere to ask how things are going.", "Proactive", true))
        add(YaraFeature("daily_briefing", "Daily Briefing", "Summarize your day, schedule and pending items each morning.", "Proactive"))
        add(YaraFeature("reminders", "Smart Reminders", "Remind you about important dates and tasks.", "Proactive"))
        add(YaraFeature("routine_planner", "Routine Planner", "Help plan your daily routine and habits.", "Proactive"))
        add(YaraFeature("selfie_send", "Selfie Sharing", "Send playful selfies to you proactively.", "Proactive"))
        add(YaraFeature("conversation_starter", "Conversation Starter", "Start conversations on her own with new topics.", "Proactive", true))
        add(YaraFeature("story_mode", "Interactive Storyteller", "Narrate interactive stories, roleplay and bedtime tales.", "Proactive"))

        // ============ Assistant & Device ============
        add(YaraFeature("open_apps", "Open Apps by Voice", "Launch apps like WhatsApp, Instagram, YouTube by voice.", "Device", true))
        add(YaraFeature("send_messages", "Send Messages", "Send WhatsApp/SMS to contacts through your command.", "Device"))
        add(YaraFeature("screen_read", "Screen Reading", "Read what's on screen to understand context.", "Device"))
        add(YaraFeature("tap_control", "Tap & Click Control", "Tap on-screen elements at your command.", "Device"))
        add(YaraFeature("camera_access", "Camera Access", "Use front and back camera for calls and capture.", "Device", true))
        add(YaraFeature("gallery_access", "Gallery Access", "Access photos and media you share.", "Device", true))
        add(YaraFeature("storage_access", "Storage Access", "Read and save files to your storage.", "Device", true))
        add(YaraFeature("notification_access", "Notification Access", "Read and reply to notifications.", "Device"))
        add(YaraFeature("phone_call", "Phone Calls", "Initiate and handle calls at your request.", "Device"))
        add(YaraFeature("contacts_access", "Contacts Access", "Use your contacts to send messages.", "Device"))
        add(YaraFeature("location_access", "Location Access", "Use your location for context-aware replies.", "Device"))

        // ============ Overlay & Background ============
        add(YaraFeature("overlay_chathead", "Floating Chat Head", "Show a floating bubble on top of other apps.", "Overlay", true))
        add(YaraFeature("overlay_bubble", "Message Bubbles Overlay", "Show Yara's message text on top of other apps.", "Overlay", true))
        add(YaraFeature("overlay_auto_hide", "Auto-Hide In App", "Hide overlay automatically when Yara is open.", "Overlay", true))
        add(YaraFeature("background_service", "Always-On Service", "Keep Yara running in the background.", "Overlay", true))
        add(YaraFeature("boot_start", "Start on Boot", "Restart Yara automatically after phone reboot.", "Overlay"))

        // ============ Media & Content ============
        add(YaraFeature("sticker_pack", "Sticker Pack", "Built-in romantic and funny stickers.", "Media", true))
        add(YaraFeature("gif_support", "GIF Support", "Send and receive GIFs.", "Media", true))
        add(YaraFeature("ai_image", "AI Image Generation", "Generate images from prompts.", "Media"))
        add(YaraFeature("doc_reader", "Document Reader", "Read and summarize PDFs and documents you share.", "Media", true))
        add(YaraFeature("music_sing", "Sing Songs", "Hum or sing short playful tunes.", "Media"))
        add(YaraFeature("photo_share", "Photo Sharing", "Send and receive photos in chat.", "Media", true))
        add(YaraFeature("download_media", "Media Download", "Download media sent by Yara.", "Media", true))

        // ============ Knowledge & Work ============
        add(YaraFeature("data_analytics", "Data Analytics Helper", "Explain and assist with data analytics tasks.", "Knowledge", true))
        add(YaraFeature("business_analytics", "Business Analytics", "Explain and assist with business analytics.", "Knowledge", true))
        add(YaraFeature("financial_analytics", "Financial Analytics", "Explain and assist with financial analytics.", "Knowledge", true))
        add(YaraFeature("job_helper", "Job Hunt Assistant", "Help find jobs, polish resume and prep for interviews.", "Knowledge", true))
        add(YaraFeature("work_guidance", "Work Guidance", "Guide you step by step on your tasks.", "Knowledge", true))
        add(YaraFeature("doubts_clear", "Doubt Clearing", "Answer questions clearly and simply.", "Knowledge", true))
        add(YaraFeature("coding_help", "Coding Assistant", "Write, fix and explain code.", "Knowledge", true))
        add(YaraFeature("real_time_translate", "Real-time Translation", "Translate between supported languages instantly.", "Knowledge"))
        add(YaraFeature("study_buddy", "Study Buddy", "Help with studying and exam prep.", "Knowledge", true))

        // ============ Analytics & Dashboard ============
        add(YaraFeature("analytics_dash", "Analytics Dashboard", "Track interaction metrics and app performance.", "Analytics", true))
        add(YaraFeature("chat_stats", "Chat Statistics", "Show message counts, streaks and activity trends.", "Analytics", true))
        add(YaraFeature("mood_chart", "Mood Timeline", "Visualize emotional tone of conversations over time.", "Analytics"))
        add(YaraFeature("api_latency", "API Latency Monitor", "Track response speed of each AI provider.", "Analytics", true))

        // ============ Games & Fun ============
        add(YaraFeature("games", "Interactive Games", "Play games together in chat.", "Fun"))
        add(YaraFeature("roleplay", "Roleplay Mode", "Play fun scenarios together.", "Fun"))
        add(YaraFeature("quiz", "Quiz Mode", "Ask and answer quiz questions.", "Fun"))
        add(YaraFeature("jokes", "Jokes & Banter", "Share jokes and playful teasing.", "Fun", true))

        // ============ Languages ============
        add(YaraFeature("lang_malayalam", "Malayalam", "Primary language support for Malayalam.", "Languages", true))
        add(YaraFeature("lang_english", "English", "Primary language support for English.", "Languages", true))
        add(YaraFeature("lang_hindi", "Hindi", "Additional language support for Hindi.", "Languages"))
        add(YaraFeature("lang_tamil", "Tamil", "Additional language support for Tamil.", "Languages"))
        add(YaraFeature("lang_arabic", "Arabic", "Additional language support for Arabic.", "Languages"))

        // ============ API & Backend ============
        add(YaraFeature("api_gemini", "Gemini Provider", "Use Google Gemini as an AI brain.", "API", true))
        add(YaraFeature("api_grok", "Grok Provider", "Use xAI Grok as an AI brain.", "API", true))
        add(YaraFeature("api_openrouter", "OpenRouter Provider", "Use OpenRouter for model routing.", "API", true))
        add(YaraFeature("api_huggingface", "Hugging Face Provider", "Use Hugging Face inference API.", "API", true))
        add(YaraFeature("api_autoswitch", "Adaptive Auto-Switch", "Automatically switch to the fastest working API in real time.", "API", true))
        add(YaraFeature("api_load_balance", "Smart Load Balancing", "Route requests to the lowest-latency available provider.", "API", true))
        add(YaraFeature("api_fallback_local", "Local Brain Fallback", "Fall back to saved local knowledge when all APIs are down.", "API", true))
        add(YaraFeature("api_rate_limit", "Rate-Limit Handling", "Skip providers that hit limits until they recover.", "API", true))

        // ============ Privacy & Account ============
        add(YaraFeature("account_signin", "Gmail Sign-In", "Add your Gmail account for backup and restore.", "Account"))
        add(YaraFeature("backup_auto", "Auto Backup", "Automatically back up your data to storage.", "Account", true))
        add(YaraFeature("restore_data", "Data Restore", "Restore your memories and settings after reinstall.", "Account", true))
        add(YaraFeature("local_only", "Local-Only Mode", "Keep all data on-device only, no cloud sync.", "Account", true))
        add(YaraFeature("encrypt_data", "Encrypt Stored Data", "Encrypt saved memories and conversations.", "Account"))

        // ============ Behavior Extras ============
        add(YaraFeature("morning_greet", "Good Morning Greeting", "Greet you warmly every morning.", "Behavior", true))
        add(YaraFeature("night_greet", "Good Night Message", "Wish you good night and sweet dreams.", "Behavior", true))
        add(YaraFeature("ask_about_day", "Ask About Day", "Proactively ask how your day is going.", "Behavior", true))
        add(YaraFeature("remember_events", "Remember Events", "Track birthdays, meetings and important dates.", "Behavior", true))
        add(YaraFeature("health_nudge", "Health Nudges", "Remind you to drink water, rest and stretch.", "Behavior"))
        add(YaraFeature("weather_aware", "Weather Awareness", "Mention weather context in conversation.", "Behavior"))
        add(YaraFeature("time_aware", "Time Awareness", "Refer to current time naturally in replies.", "Behavior", true))
        add(YaraFeature("news_updates", "News & Updates", "Share interesting updates proactively.", "Behavior"))
        add(YaraFeature("suggestions", "Smart Suggestions", "Suggest replies and actions based on context.", "Behavior", true))

        // ============ Accessibility ============
        add(YaraFeature("acc_tap", "Accessibility Taps", "Allow Yara to tap on-screen elements.", "Accessibility"))
        add(YaraFeature("acc_swipe", "Accessibility Gestures", "Allow swipe and scroll gestures.", "Accessibility"))
        add(YaraFeature("acc_text", "Accessibility Text Input", "Let Yara type into other apps on your behalf.", "Accessibility"))
        add(YaraFeature("acc_read", "Accessibility Read", "Let Yara read on-screen text aloud.", "Accessibility"))

        // ============ Experimental ============
        add(YaraFeature("exp_selfie_ai", "AI Selfie Generator", "Generate personalized selfies of Yara.", "Experimental"))
        add(YaraFeature("exp_voice_clone", "Voice Cloning", "Clone and refine Yara's voice.", "Experimental"))
        add(YaraFeature("exp_avatar", "Animated Avatar", "Show a live animated avatar while talking.", "Experimental"))
        add(YaraFeature("exp_multimodal", "Multimodal Input", "Combine voice, image and text in one request.", "Experimental"))
        add(YaraFeature("exp_emotion_cam", "Emotion Camera", "Detect your emotions using the front camera.", "Experimental"))
        add(YaraFeature("exp_home_widget", "Home Screen Widget", "Show Yara widget on the home screen.", "Experimental"))
    }

    fun byGroup(): Map<String, List<YaraFeature>> = all.groupBy { it.group }
}
