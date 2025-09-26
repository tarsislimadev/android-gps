# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android application project named "GPS" (`br.tmvdl.gps`) built with Kotlin and Android Gradle Plugin 8.13.0. Currently, it contains a functional GPS tracking application with location data management and display capabilities.

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
- Two fragments: `FirstFragment` (GPS data display) and `SecondFragment` (template)
- Navigation graph defined in `res/navigation/nav_graph.xml`
- Uses Material Design with `CoordinatorLayout`, `AppBarLayout`, and `FloatingActionButton`

### Location Services Architecture
The GPS functionality is implemented with a clean separation of concerns:
- **LocationDataManager**: Core location management class that handles all location providers
- **LocationInfo**: Data class representing location data from various providers
- **LocationData**: Container class for complete location state
- **LocationInfoAdapter**: RecyclerView adapter for displaying location information

### Key Components
- **MainActivity**: `MainActivity.kt` - Sets up toolbar and navigation
- **FirstFragment**: `FirstFragment.kt` - Main GPS interface with permission handling and location display
- **LocationDataManager**: `LocationDataManager.kt` - Manages location updates from GPS, Network, and other providers
- **LocationInfoAdapter**: `LocationInfoAdapter.kt` - RecyclerView adapter for location data display
- **Package Structure**: `br.tmvdl.gps` (Brazilian domain, personal project)

### Location Features
- **Multiple Provider Support**: GPS, Network, Passive, and other location providers
- **Real-time Updates**: Live location tracking with 1-second intervals
- **Last Known Locations**: Display of cached location data from all providers
- **GNSS Status**: Satellite count monitoring for GPS provider
- **Permission Handling**: Comprehensive location permission management
- **Mock Location Detection**: Identifies simulated/mock locations
- **Provider Status**: Real-time monitoring of enabled/disabled location providers

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

### Permissions
The app requires the following location permissions (declared in AndroidManifest.xml):
- `ACCESS_FINE_LOCATION` - For precise GPS location
- `ACCESS_COARSE_LOCATION` - For network-based location
- `ACCESS_BACKGROUND_LOCATION` - For background location access

## Development Context

This is a functional GPS tracking application that demonstrates comprehensive location services implementation. The app provides real-time location tracking from multiple providers, displays detailed location information including satellite data, and handles all location permissions properly.

The project structure follows modern Android development practices with Navigation Components, View Binding, LiveData observables, and proper lifecycle management.