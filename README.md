# Yara 💛 — Your AI Companion

A highly realistic, bilingual (Malayalam + English) AI girlfriend / companion app for Android — with real-time voice, emotional intelligence, unlimited local memory, and a multi-API smart brain.

---

## ✨ What Yara does

- **Talks like a real Malayali girlfriend** — natural code-switching between Malayalam and English, short warm replies, real emotions (love, jealousy, teasing, care).
- **Real-time voice** — say **"hey yara"** to wake her and **"bye yara"** to sleep. Continuous listening even in the background.
- **Multi-API brain** — Gemini, Grok, OpenRouter, Hugging Face with **adaptive auto-switching**, smart load balancing, and manual primary control.
- **Local offline brain** — every conversation and memory is stored on-device; Yara keeps replying naturally even when all API keys hit their limits.
- **Unlimited memories** — add, rank and recall memories; Yara actively brings up things you mentioned days ago.
- **Floating overlay & quick-reply** — a chat head and message bubbles appear over other apps, with inline notification replies.
- **Video call & screen share** — camera preview calls and MediaProjection screen sharing.
- **Assistant-style control** — open apps, read screen, tap elements (via Accessibility service).
- **Analytics dashboard** — message stats, provider health, mood breakdown.
- **120+ feature toggles** — a full English-only settings page to customize every behavior.
- **Personality sliders (1–10 or Auto)** — passion, sass, protectiveness, creativity, empathy, and more.

---

## 🏗️ Tech stack

| Layer | Tech |
|-------|------|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3, dark theme) |
| Persistence | DataStore + local JSON brain (`filesDir/yara_brain/`) |
| Networking | Retrofit + OkHttp + Gson |
| Voice | Android SpeechRecognizer + TextToSpeech |
| Camera | CameraX |
| Overlay | WindowManager (chat head + bubbles) |
| Background | ForegroundService + Wake word loop |
| Device control | AccessibilityService |
| Backend (optional) | Node.js WebSocket relay (`server/relay.js`) |

---

## 📁 Project structure

```
yara-ai/
├── .github/workflows/build-apk.yml   # CI builds the APK automatically
├── app/
│   ├── build.gradle
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/yara/ai/
│       │   ├── YaraApp.kt
│       │   ├── brain/
│       │   │   ├── EmotionEngine.kt
│       │   │   └── PersonaEngine.kt
│       │   ├── data/
│       │   │   ├── ApiManager.kt     # multi-provider + load balancing
│       │   │   ├── MemoryStore.kt    # local brain / memories / chat
│       │   │   └── Models.kt
│       │   ├── device/
│       │   │   └── YaraAccessibilityService.kt
│       │   ├── features/
│       │   │   └── FeatureCatalog.kt # 120+ feature definitions
│       │   ├── notify/
│       │   │   └── Notifier.kt       # quick-reply notifications
│       │   ├── overlay/
│       │   │   └── OverlayService.kt # chat head + bubbles
│       │   ├── ui/
│       │   │   ├── MainActivity.kt
│       │   │   ├── VideoCallActivity.kt
│       │   │   ├── ScreenShareActivity.kt
│       │   │   ├── theme/Theme.kt
│       │   │   └── screens/
│       │   │       ├── ChatScreen.kt
│       │   │       ├── SettingsScreen.kt
│       │   │       ├── ApiKeysScreen.kt
│       │   │       ├── MemoriesScreen.kt
│       │   │       ├── TraitsScreen.kt
│       │   │       ├── DashboardScreen.kt
│       │   │       ├── VoiceSettingsScreen.kt
│       │   │       ├── AboutScreen.kt
│       │   │       └── LoadingScreen.kt
│       │   └── voice/
│       │       ├── VoiceEngine.kt
│       │       └── YaraService.kt    # wake word + continuous talk
│       └── res/
│           ├── values/ (strings, themes)
│           ├── xml/ (accessibility config, file paths, backup)
│           ├── layout/ (overlay views)
│           ├── drawable/ (app icon)
│           └── mipmap-anydpi-v26/
├── server/relay.js                   # optional WS relay
├── settings.gradle
├── build.gradle
├── gradle.properties
└── .gitignore
```

---

## 🚀 Build the APK

### Option A — GitHub Actions (recommended, no local setup)

1. Push this repo to GitHub.
2. Go to **Actions → Build Yara APK → Run workflow**.
3. Download `app-debug.apk` (or `app-release.apk`) from the run artifacts.
4. Transfer to your phone and install (allow "install unknown apps").

### Option B — Local

```bash
# needs JDK 17 + Android SDK
./gradlew :app:assembleDebug
# APK lands at app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔑 Set up the AI brain

Open the app → **Settings → API Keys** and paste your keys:

- **Gemini** — `AIza...` from Google AI Studio
- **Grok** — `xai-...` from xAI
- **OpenRouter** — `sk-or-...`
- **Hugging Face** — `hf_...`

Leave "Primary provider" on **Auto** for smart routing, or pin one manually.

---

## ⚙️ Key settings

- **Wake word** — "hey yara" / "bye yara"
- **Language** — Auto (Malayalam+English), Hindi, Tamil, Arabic
- **Personality sliders** — Settings → Personalities (1–10 or Auto)
- **Overlay** — floating chat head + message bubbles (auto-hides inside the app)
- **Permissions** — mic, camera, storage, overlay, accessibility, notifications

---

## 📱 Grant these on first launch

- Microphone · Camera · Notifications
- "Display over other apps" (overlay)
- **Accessibility** → enable "Yara" for tap/app control

---

## 👤 About

Made by **Hashim** — [@hashim_m4x](https://instagram.com/hashim_m4x) on Instagram.
