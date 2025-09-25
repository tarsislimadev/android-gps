# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android application project named "GPS" (`br.tmvdl.gps`) built with Kotlin and Android Gradle Plugin 8.13.0. Currently, it contains only the standard Android Studio template structure with Navigation Components - no actual GPS functionality has been implemented yet.

## Build Commands

### Standard Android Development
- **Build the project**: `./gradlew build` (Linux/macOS) or `gradlew.bat build` (Windows)
- **Install debug APK**: `./gradlew installDebug`
- **Clean build**: `./gradlew clean`
- **Run unit tests**: `./gradlew test`
- **Run instrumented tests**: `./gradlew connectedAndroidTest`
- **Generate APK**: `./gradlew assembleDebug` or `./gradlew assembleRelease`

### Lint and Code Quality
- **Run lint checks**: `./gradlew lint`
- **Kotlin code style**: No specific formatter configured - uses default Kotlin conventions

## Architecture & Structure

### Navigation Architecture
The app uses **Android Navigation Component** with a single-activity architecture:
- `MainActivity` hosts the navigation controller
- Two template fragments: `FirstFragment` and `SecondFragment` with bidirectional navigation
- Navigation graph defined in `res/navigation/nav_graph.xml`
- Uses Material Design with `CoordinatorLayout`, `AppBarLayout`, and `FloatingActionButton`

### Key Components
- **Main Activity**: `MainActivity.kt` - Sets up toolbar, navigation, and FAB with placeholder action
- **Fragments**: Standard template fragments using View Binding pattern
- **Package Structure**: `br.tmvdl.gps` (Brazilian domain, likely personal project)

### Build Configuration
- **Gradle Version Catalog**: Dependencies managed in `gradle/libs.versions.toml`
- **Target/Compile SDK**: 36 (latest)
- **Min SDK**: 24 (Android 7.0)
- **View Binding**: Enabled
- **Kotlin**: 2.0.21
- **Java Version**: 11

### Dependencies
Standard Android template dependencies:
- AndroidX Core KTX, AppCompat
- Material Components
- ConstraintLayout  
- Navigation Fragment & UI KTX
- JUnit, Espresso for testing

## Development Context

This appears to be a fresh Android project template that was created with the intention of building a GPS-related application. No location services, maps integration, or GPS functionality exists yet - making this a blank slate for GPS feature development.

The project structure follows modern Android development practices with Navigation Components, View Binding, and Kotlin-first approach.