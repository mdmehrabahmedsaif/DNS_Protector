# 🛡️ DNS Protector

[![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B%20(API%2024%2B)-brightgreen.svg)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Java%2017-orange.svg)](https://www.oracle.com/java/)
[![Version](https://img.shields.io/badge/Release-v1.0.0-blue.svg)](CHANGELOG.md)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Build](https://img.shields.io/badge/Build-Docker%20%7C%20Gradle-blueviolet.svg)](HowToSetup.md)

**DNS Protector** is a native, offline Android security application engineered to support individuals in digital self-discipline and recovery from pornography addiction. It enforces unbreakable Private DNS protections, ensuring users cannot easily bypass content filters (such as **NextDNS**, **AdGuard**, or **CleanBrowsing**) during moments of behavioral relapse.

---

## 🚀 Key Features

- **⚡ Automated DNS Settings Blocker:**
  Runs an efficient background `AccessibilityService` that detects when the Android system Private DNS popup (`"Select Private DNS Mode"`) is opened and immediately executes an automated back action to dismiss it.
- **🔒 Anti-Uninstall Device Administrator:**
  Registers as an active Device Administrator via `DevicePolicyManager`, stopping impulsive drag-and-drop or settings-based uninstalls.
- **🌙 Sleek Dark-Mode Dashboard:**
  Modern cyber-security aesthetic featuring real-time status rings, responsive health indicators, and direct permission toggles.
- **🛡️ 100% Offline & Private:**
  No internet permission requested. Zero tracking, zero telemetry, zero analytics. All enforcement happens entirely on-device.
- **🔋 Battery Efficient:**
  No background polling or heavy CPU loops. Intercepts events event-driven through the native Android accessibility framework.

---

## 🏗️ Architecture & How It Works

```
[ User attempts to open Settings > Private DNS ]
                        │
                        ▼
    [ PrivateDnsBlockerService (Accessibility) ]
                        │
       Is window title "Select Private DNS Mode"?
                        ├───► YES: Fires GLOBAL_ACTION_BACK (Dialog Closed!)
                        └───► NO:  Ignores event (Safe)
```

1. **Accessibility Interception:** [`PrivateDnsBlockerService.java`](app/src/main/java/com/dns/protector/PrivateDnsBlockerService.java) continuously listens for window state changes from `com.android.settings`.
2. **Device Administration:** [`DNSProtectorAdminReceiver.java`](app/src/main/java/com/dns/protector/DNSProtectorAdminReceiver.java) prevents uninstallation of the package while administrative policy remains active.
3. **State Management:** [`MainActivity.java`](app/src/main/java/com/dns/protector/MainActivity.java) manages permission workflows and synchronizes user preferences via `SharedPreferences`.

---

## 📁 Repository Structure

```
DNS_Protector/
├── .env.example              # Environment variables template
├── CLAUDE.md                 # Agent & AI tool instructions
├── AGENTS.md                 # Developer & AI guide
├── CHANGELOG.md              # Version release history
├── Dockerfile                # Docker build container definition
├── docker-compose.yml        # Docker Compose configuration
├── HowToSetup.md             # Detailed installation and setup guide
├── README.md                 # Project documentation (this file)
├── build.gradle              # Project build configuration
├── settings.gradle           # Project settings
├── database/
│   ├── schema.sql            # Relational database schema
│   └── seed.sql              # Default seed data and demo logs
└── app/
    ├── build.gradle          # Module configuration
    └── src/main/             # Android application source code
```

---

## ⚡ Quick Start

### 1. Build with Docker (Recommended)
You do not need Android Studio installed:
```bash
docker compose up --build
```
Your compiled APK will be output to `./build-output/`.

### 2. Build with Gradle CLI
```bash
gradle assembleRelease --no-daemon
```

For complete step-by-step setup guides on a VPS or clean machine, see [HowToSetup.md](HowToSetup.md).

---

## 📝 License
This project is open-source and distributed under the [MIT License](LICENSE).
