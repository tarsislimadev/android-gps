# GPS Android Application

An Android application for GPS functionality built with Kotlin and modern Android development practices.

Just open this project on Cursor IDE in PowerShell.

READ ALL FILE

## Project Information

- **Package Name**: `br.tmvdl.gps`
- **Target SDK**: 36
- **Minimum SDK**: 24 (Android 7.0+)
- **Language**: Kotlin 2.0.21
- **Architecture**: Single Activity with Navigation Components

## Features

This project is currently in initial development phase with a template structure ready for GPS feature implementation.

## Build Requirements

- Android Studio Arctic Fox or newer
- JDK 11
- Android SDK 36

## Build Commands

### Development
```bash
# Build the project
./gradlew build

# Install debug APK
./gradlew installDebug

# Clean build
./gradlew clean
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

### APK Generation
```bash
# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease
```

### Code Quality
```bash
# Run lint checks
./gradlew lint
```

## Architecture

The application uses:
- **Navigation Components** for fragment navigation
- **View Binding** for type-safe view references
- **Material Design** components
- **Single Activity** architecture pattern

## Project Structure

```
app/
├── src/main/java/br/tmvdl/gps/
│   ├── MainActivity.kt
│   ├── FirstFragment.kt
│   └── SecondFragment.kt
├── src/main/res/
│   ├── layout/
│   ├── navigation/nav_graph.xml
│   └── values/
└── build.gradle.kts
```

## Dependencies

Key dependencies include:
- AndroidX Core KTX
- Material Components
- Navigation Components
- ConstraintLayout
- JUnit & Espresso for testing

Dependencies are managed using Gradle Version Catalog in `gradle/libs.versions.toml`.

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

## Development Status

This project contains the initial Android template structure with Navigation Components. GPS functionality implementation is pending.