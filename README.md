# 📱 Mononote for Android

> **Digital minimalism for Android. One active note, zero folder clutter, instant auto-save, true AMOLED black, and a persistent "🔴 Go Live" Lock Screen & Status Bar card.**

Optimized for **Redmi Note 11 (AMOLED 90Hz)** and **Android 11+ (API 26–35)**.

---

## ✨ Features

- 🎯 **The Single Canvas**: Launches straight into the editor with keyboard ready (<35ms cold start). No notebooks, folders, or tag chaos.
- 🔴 **"Go Live" Engine**: Tap *Go Live* to pin your active note directly to your **Lock Screen & Status Bar** with interactive `[✓ Done]` and `[📋 Copy]` action buttons.
- 🖤 **True AMOLED Pitch-Black (`#000000`)**: Saves battery on AMOLED displays and runs at full 90 FPS.
- 🔤 **3 Typography Engines**: Switch on the fly between **Sans** (Default), **Mono** (Technical/Markdown), and **Serif** (Editorial).
- 🧩 **Jetpack Glance Widget**: Interactive Home Screen sticky note widget that updates live.
- ⚡ **Control Center Quick Tile**: Pull down from the top shade $\rightarrow$ tap *Mononote* for instant capture over any app.
- 🪟 **MIUI / HyperOS Floating Window**: Full freeform multi-window support (`resizeableActivity = true`).
- 📦 **Archive & Fresh Canvas**: One tap archives your completed note to a quiet historical timeline and gives you a fresh blank canvas.
- 🔒 **100% Offline & Private**: Stored locally in SQLite via Room. Zero accounts, zero analytics, zero network requests.

---

## 🛠️ Tech Stack

- **Language**: Kotlin 2.1
- **UI Framework**: Jetpack Compose + Material 3
- **Widgets**: Jetpack Glance (AppWidgets)
- **Persistence**: Room Database + SQLite
- **Architecture**: MVVM with reactive `StateFlow` and Coroutines
- **System Integration**: `NotificationManager` ongoing Live Card, `TileService` Quick Settings, `GlanceAppWidgetReceiver`

---

## 🚀 How to Run in Android Studio

1. Open **Android Studio**.
2. Click **Open** $\rightarrow$ Navigate to:
   ```
   C:\Users\anshu\.gemini\antigravity\scratch\mononote-android
   ```
3. Allow Gradle to sync dependencies.
4. Connect your **Redmi Note 11** via USB (with USB Debugging enabled) or choose an Android Emulator.
5. Hit **Run (Shift + F10)**!

---

## 🔋 Redmi Note 11 (MIUI / HyperOS) Tip

To ensure MIUI never kills your Live Notification card in the background:
1. Long-press the **Mononote** icon $\rightarrow$ **App info**.
2. Set **Battery saver** to **"No restrictions"**.
3. Enable **Autostart**.

---

## 📄 License
MIT License
