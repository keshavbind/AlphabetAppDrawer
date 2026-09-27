# Alphabet App Drawer 📱✨

A modern, fluid Android app drawer built with **Jetpack Compose** and **Material 3**. Featuring a custom physics-driven curved alphabet scroller, interactive touch feedback, and instant launcher integration for all installed apps.

---

## 🌟 Key Features

- **🌊 Fluid Physics-Based Curved Scroller**: A custom drag-driven side index bar (A–Z) that responds with Gaussian curve displacement, spring animations, dynamic scaling, and letter rotation as your finger glides over it.
- **🎈 Floating Preview Bubble**: A sleek pop-out bubble that gives real-time visual feedback of the currently selected letter while dragging.
- **📱 Installed App Querying**: Automatically queries and filters all launcher-enabled applications installed on the Android device.
- **⚡ Instant App Launching**: Launch any app with a single tap directly from the drawer interface.
- **🌙 Modern Dark UI**: Designed with a clean, dark-mode color palette (`#0B0B0F`), soft rounded card rows, and smooth Compose list performance.

---

## 🎥 Video Demo & Screenshots

### 📹 App Preview Video

<video src="screenshots/AlphabetAppDrawer_video.mp4" controls width="100%" poster="screenshots/Screenshot%201.jpeg"></video>

*If the video above does not play automatically in your Markdown viewer, you can view the video file directly at [screenshots/AlphabetAppDrawer_video.mp4](screenshots/AlphabetAppDrawer_video.mp4).*

### 📸 Screenshots

| Alphabet Fast Scroller | Curved Gesture & Bubble |
|:----------------------:|:-----------------------:|
| ![Screenshot 1](screenshots/Screenshot%201.jpeg) | ![Screenshot 2](screenshots/Screenshot%202.jpeg) |

---

## 🛠️ Tech Stack & Requirements

| Layer / Tool | Technology |
|---|---|
| **Language** | Kotlin 1.9+ |
| **UI Framework** | Jetpack Compose (Material 3) |
| **Min SDK** | Android 8.0 (API Level 26) |
| **Target SDK** | Android 15 (API Level 35) |
| **Build System** | Gradle with Kotlin DSL (`build.gradle.kts`) |
| **Architecture** | Single-Activity Declarative UI Architecture |

---

## 📂 Project Structure

```text
AlphabetAppDrawer/
├── app/
│   ├── build.gradle.kts          # App-level dependencies & Compose configuration
│   └── src/main/
│       ├── AndroidManifest.xml   # Query permissions & main activity configuration
│       └── java/com/example/alphabetappdrawer/
│           ├── MainActivity.kt   # Core UI Composables, launcher logic, & curved scroller physics
│           └── ui/theme/         # Material 3 theme definitions
├── screenshots/                  # Preview media assets
│   ├── AlphabetAppDrawer_video.mp4 # Video demo of app drawer in action
│   ├── Screenshot 1.jpeg         # Screenshot preview
│   └── Screenshot 2.jpeg         # Screenshot preview
├── build.gradle.kts              # Root build configuration
├── settings.gradle.kts           # Gradle repository & module management
└── README.md                     # Project documentation
```

---

## ⚙️ How It Works

1. **App Discovery (`getInstalledApps`)**:
   Uses `PackageManager.queryIntentActivities` with `Intent.ACTION_MAIN` and `Intent.CATEGORY_LAUNCHER` to discover user-launchable apps, filtering out system background processes and organizing apps alphabetically.
2. **Custom Scroller Physics (`AlphabetBar` & `CurvedLetter`)**:
   Calculates touch position relative to the index bar height and applies a Gaussian distribution curve (`exp(-d² / 2σ²)`) to dynamically offset ($x, y$), scale, and rotate nearby letters on the fly with spring dampening (`Spring.DampingRatioNoBouncy`).
3. **Interactive Bubble (`LetterBubble`)**:
   Tracks vertical touch coordinates in real time (`pointerInput` & `detectDragGestures`) to align a floating circular bubble alongside the user's thumb.

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Koala / Ladybug / Jellyfish or newer recommended)
- **JDK 11** or higher
- Android Device or Emulator running **Android 8.0 (API 26)** or higher

### Building & Running
1. Clone the repository:
   ```bash
   git clone https://github.com/keshavbind/AlphabetAppDrawer.git
   cd AlphabetAppDrawer
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle dependencies.
4. Run on an attached device or emulator:
   ```bash
   ./gradlew installDebug
   ```

---

## 📜 License

This project is open-source and available under the [MIT License](LICENSE).
