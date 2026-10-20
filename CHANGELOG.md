# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-10-20

### Added
- **Core Accessibility Blocker Service (`PrivateDnsBlockerService`):**
  - Monitors window state and window content change events from `com.android.settings`.
  - Implemented fast inspection via direct event text matching for `"Select Private DNS Mode"`.
  - Implemented deep inspection via recursive `AccessibilityNodeInfo` tree traversal to detect dialog popups across different Android OEM skins (Pixel, Samsung OneUI, Xiaomi MIUI, OxygenOS).
  - Automatically executes `GLOBAL_ACTION_BACK` upon target popup identification.
- **Anti-Uninstall Device Administrator (`DNSProtectorAdminReceiver`):**
  - Integrates with Android `DevicePolicyManager` to prevent uninstallation from the application drawer or system settings.
  - Policy configuration registered with declarative device administration metadata.
- **Dark-Theme Control Dashboard (`MainActivity`):**
  - Real-time status display with dynamic green (ACTIVE) and red (INACTIVE) status rings and indicators.
  - One-tap "HALT" / "ENABLE" triggers for Accessibility Service and Device Administrator permissions.
  - Added delayed activity reordering (`bringAppToFrontDelayed`) using `FLAG_ACTIVITY_REORDER_TO_FRONT` to seamlessly return the user to the app after system settings changes.
  - Persistent master toggle switch backed by Android `SharedPreferences`.
- **Packaging & Deployment:**
  - Automated isolated Docker build via `Dockerfile` and `docker-compose.yml`.
  - Multi-target setup instructions in `HowToSetup.md` covering VPS/Linux clean machine and Docker environments.
  - Relational database schema (`database/schema.sql`) and seed dataset (`database/seed.sql`) for audit trails and DNS profile configurations.
  - Configuration template with `.env.example`.
  - Comprehensive documentation and developer guides (`README.md`, `CLAUDE.md`, `AGENTS.md`).

### Security
- Designed with 100% offline architecture: zero network tracking, zero third-party analytical SDKs, completely preserving user privacy and anonymity.
- Enforced strict node recycling (`node.recycle()`) across accessibility tree traversals to eliminate native memory leaks and ensure long-term background stability.
