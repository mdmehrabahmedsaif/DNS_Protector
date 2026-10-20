# CLAUDE.md — Agent & Developer Guide for DNS Protector

This document serves as the architectural reference, developer cheat sheet, and convention guide for AI coding agents and human engineers contributing to **DNS Protector**.

---

## 1. Project Overview & Motivation

DNS Protector is a specialized native Android security application designed to assist individuals in recovering from pornography and digital addiction by enforcing strict Private DNS lockouts. 

It prevents users from bypassing content blockers (such as **NextDNS**, **AdGuard**, or **CleanBrowsing**) in moments of relapse.

### Key Capabilities:
- **Heuristic Settings Interceptor:** Runs an `AccessibilityService` monitoring `com.android.settings`. If a dialog titled `"Select Private DNS Mode"` appears, it executes `GLOBAL_ACTION_BACK` immediately.
- **Device Administrator Anti-Uninstall:** Employs Android `DevicePolicyManager` to lock uninstallation attempts.
- **Ultra-low Footprint:** 100% offline, zero network telemetry, zero background battery drain, uses native Java without bloated third-party dependencies.

---

## 2. Tech Stack & Environment

- **Language:** Java 17
- **Target OS:** Android 7.0 (API Level 24) to Android 14 (API Level 34)
- **Compile SDK:** 34
- **Build System:** Gradle (with Android Gradle Plugin 8.2+)
- **UI Architecture:** Native Android XML layouts with AndroidX AppCompat (`1.6.1`) and Google Material Design Components (`1.11.0`)
- **Persistence:** Local Android `SharedPreferences` + Relational SQL Schema (`database/schema.sql`)

---

## 3. Directory & Folder Map

```
DNS_Protector/
├── .env.example                     # Environment and configuration templates
├── CLAUDE.md                        # AI developer and agent guide (this file)
├── CHANGELOG.md                     # Semantic version history
├── Dockerfile                       # Containerized build definition (Java 17 + Android SDK 34)
├── docker-compose.yml               # Automated container build definition
├── HowToSetup.md                    # Setup, build, installation & branding guide
├── README.md                        # Primary project documentation
├── build.gradle                     # Top-level Gradle build script
├── settings.gradle                  # Gradle project & repository resolution configuration
├── gradle.properties                # JVM & AndroidX build parameters
├── database/
│   ├── schema.sql                   # SQL database schema definition
│   └── seed.sql                     # Seed data with default DNS profiles & audit logs
└── app/
    ├── build.gradle                 # App-level dependencies, SDK targets, and build types
    ├── proguard-rules.pro           # ProGuard/R8 obfuscation and optimization rules
    └── src/main/
        ├── AndroidManifest.xml      # Manifest registering Services, Receivers & Permissions
        ├── java/com/dns/protector/
        │   ├── MainActivity.java            # Main UI, status indicators, and permission controllers
        │   ├── PrivateDnsBlockerService.java # AccessibilityService intercepting settings dialog
        │   └── DNSProtectorAdminReceiver.java# DeviceAdminReceiver locking uninstall
        └── res/
            ├── drawable/            # Vector graphics, icons, and custom button borders
            ├── layout/
            │   └── activity_main.xml# Dark-mode dashboard layout
            ├── values/
            │   └── strings.xml      # Translatable strings & permission descriptions
            └── xml/
                ├── accessibility_service_config.xml # Accessibility filter declarations
                └── device_admin_policies.xml       # Device Admin policy declaration
```

---

## 4. Build, Test & Lint Commands

### Compiling from Command Line:
```bash
# Clean build artifacts
gradle clean

# Build debug APK
gradle assembleDebug

# Build production release APK
gradle assembleRelease --no-daemon
```

### Running Tests & Lint Checks:
```bash
# Run unit tests
gradle test

# Run Android Lint analysis
gradle lint

# Run instrumentation tests (requires connected emulator/device)
gradle connectedAndroidTest
```

---

## 5. Architectural Principles & Coding Conventions

### 1. Accessibility Event Efficiency
- `PrivateDnsBlockerService.java` is called on every window event. Keep evaluation fast:
  1. Filter by package name first: `com.android.settings`.
  2. Perform shallow checks on `event.getText()` before scanning the recursive `AccessibilityNodeInfo` tree.
  3. **Always call `.recycle()`** on any acquired `AccessibilityNodeInfo` instances to prevent native memory leaks.

### 2. Device Admin Safety
- Do not request invasive administrative policies (e.g., remote wipe). 
- An empty `<uses-policies>` block in `device_admin_policies.xml` is sufficient to prevent app uninstallation while keeping user trust intact.

### 3. Java Style Guide
- Follow standard Google Java Style Conventions:
  - 4-space indentation.
  - Standard camelCase for method and variable names; UPPER_SNAKE_CASE for constants.
  - Descriptive private logging tags (e.g., `private static final String TAG = "PrivateDnsBlocker";`).

### 4. Zero Sideloaded Telemetry
- Maintain complete user privacy. Never add `android.permission.INTERNET` unless explicitly requested by the user. All protection checks must remain 100% on-device.
