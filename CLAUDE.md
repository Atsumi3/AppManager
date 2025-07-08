# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AppManager is an Android application that helps users select and uninstall unwanted applications. It provides two variants to handle Android's package visibility restrictions introduced in API 30 (Android 11).

### Key Architecture
- **Language**: Kotlin
- **UI Framework**: RecyclerView-based (Compose dependencies present but not fully utilized)
- **Pattern**: MVVM with ViewModels and StateFlow
- **Build System**: Gradle with Kotlin DSL and version catalogs
- **Product Flavors**:
  - `under29`: API 21-29 (no package visibility restrictions)
  - `over30`: API 30+ (handles package visibility restrictions)

## Common Development Commands

### Building the Project
```bash
# Build all variants
./gradlew build

# Build specific variants
./gradlew assembleOver30Debug    # Debug APK for API 30+
./gradlew assembleUnder29Debug   # Debug APK for API 29-
./gradlew assembleRelease        # Release APKs for all variants

# Clean build
./gradlew clean build
```

### Running Tests
```bash
# Run all tests
./gradlew test

# Run connected device tests
./gradlew connectedAndroidTest

# Run variant-specific tests
./gradlew testOver30Debug
./gradlew testUnder29Debug
```

### Linting
```bash
# Run lint checks
./gradlew lint

# Run lint with automatic fixes
./gradlew lintFix

# Variant-specific lint
./gradlew lintOver30Debug
./gradlew lintUnder29Debug
```

### Installing on Device
```bash
# Install debug builds
./gradlew installOver30Debug    # For devices API 30+
./gradlew installUnder29Debug   # For devices API 29-
```

## Code Structure

### Main Components
- **`app/src/main/java/atsumi/android/appmanager/`**:
  - `entity/` - Data models (AppInfo, AppType)
  - `ui/app_info/` - UI components and ViewModels
  - `util/` - Utility classes (DisplayCondition interface)

### Build Variants
- **`app/src/over30/`** - Code specific to API 30+ (handles QUERY_ALL_PACKAGES permission)
- **`app/src/under29/`** - Code for API 29 and below

### Key Files
- `app/build.gradle` - App module configuration with flavor definitions
- `gradle/libs.versions.toml` - Centralized dependency versions
- `.github/workflows/android-release.yaml` - CI/CD for automated releases

## Release Process

Release builds are automatically created when pushing git tags:
1. Tag the commit: `git tag vX.Y.Z`
2. Push the tag: `git push origin vX.Y.Z`
3. GitHub Actions will:
   - Build APKs for both variants
   - Generate QR codes for downloads
   - Create a PR to update README with new QR codes

Manual release build:
```bash
./gradlew assembleRelease
```

Release signing configuration is stored in `app/signingConfigs/release.jks`.

## Important Notes

- The app filters out applications targeting SDK 26 or higher
- It excludes itself from the app list
- Two separate APKs are distributed due to Android 11's package visibility restrictions
- The `over30` variant requires `QUERY_ALL_PACKAGES` permission to list all apps
