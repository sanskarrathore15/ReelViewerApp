# Reel Viewer

## Description 
A dynamic Android application designed for seamless short-form video viewing, featuring automatic scrolling, interactive elements, and Firebase backend integration.

## Table of Contents
- [Features](#features)
- [Technical Stack](#technical-stack)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)

## Features

### Core Video Features
* **Auto-Scroll Functionality**
  - Automatic transition to next reel upon completion
  - Smooth scrolling animation between reels

* **Video Controls**
  - Full-screen video playback
  - Video looping capability
  - State preservation during configuration changes

### Interactive Elements
* **Like System**
  - Interactive like button for each reel
  - Visual feedback on interaction

* **Share Functionality**
  - Direct link sharing capability
  - Multiple platform sharing support

* **Download Feature**
  - Direct mobile download capability
  - Progress tracking notification
  - Permission handling for storage access

### Backend Implementation
* **Firebase Integration**
  - Real-time data synchronization
  - Content delivery optimization
  - Data structure:
    ```
    videos/
    ├── title
    ├── description
    └── url
    ```

## Technical Stack

### Frontend
* Android Native (Kotlin)
* VideoView for playback
* ViewPager2 for smooth navigation
* MediaPlayer for video control

### Backend
* Firebase Realtime Database
* Firebase Storage (for video content)

## Project Structure

```
com.example.reels/
├── MainActivity.kt           // Main application entry
├── ReelFragment.kt          // Video playback handling
├── adapter/
│   └── VideoAdapter.kt      // Firebase data adapter
└── model/
    └── VideoModel.kt        // Data structure
```

## Installation

1. **Clone Repository**
   ```bash
   git clone [repository-url]
   ```

2. **Firebase Setup**
   - Create Firebase project
   - Add Android app
   - Download `google-services.json`
   - Add to app directory

3. **Android Studio Configuration**
   - Open project
   - Sync Gradle files
   - Build project

## Usage

### Required Permissions
```xml
download: Read and Write

```

### Key Implementation Snippets

#### Video Fragment Implementation
```kotlin
class ReelFragment : Fragment() {
    private lateinit var videoView: VideoView
    private lateinit var videoUri: Uri
    private var mediaPlayer: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoView = view.findViewById(R.id.video_view)
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            videoView.start()
            this.mediaPlayer = mediaPlayer
        }
    }
}
```

#### Download Implementation
```kotlin
private fun startDownload(obj: VideoModel) {
    val request = DownloadManager.Request(Uri.parse(obj.url))
        .setTitle("${obj.title}.mp4")
        .setDescription("${obj.title} is downloading...")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedOverMetered(true)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${obj.title}.mp4")

    val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    isDownloaded = dm.enqueue(request)
}
```

## Configuration

### Firebase Database Rules
```json
{
  "rules": {
    "videos": {
      ".read": true,
      ".write": "auth != null"
    }
  }
}
```

### Build Configuration
```gradle
dependencies {
    implementation 'com.google.firebase:firebase-database-ktx'
    implementation 'com.firebase.ui:firebase-ui-database'
}
```

## Additional Features

### Implemented
- [x] VideoPager2
- [x] ReelFragment
- [x] Firebase as backend
- [x] Splash screen
- [x] Custom app logo
- [x] Video caching
- [x] Error handling



