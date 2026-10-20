# Setup & Deployment Guide — DNS Protector

This document provides step-by-step instructions to set up, build, deploy, configure, and customize **DNS Protector** from a clean machine across multiple environments.

---

## Table of Contents
1. [Target 1: VPS / Clean Machine (Native Build)](#target-1-vps--clean-machine-native-build)
2. [Target 2: Docker Container (Automated Isolated Build)](#target-2-docker-container-automated-isolated-build)
3. [Database Setup & Migrations](#database-setup--migrations)
4. [First Admin Login & Security Activation](#first-admin-login--security-activation)
5. [Branding & Customization Guide](#branding--customization-guide)
6. [Troubleshooting & Verification](#troubleshooting--verification)

---

## Target 1: VPS / Clean Machine (Native Build)

Use this method to compile the application on an Ubuntu/Debian VPS, Linux server, or a developer workstation without a GUI.

### 1.1 Prerequisites Installation
Update package lists and install OpenJDK 17 and utility tools:
```bash
sudo apt update && sudo apt install -y openjdk-17-jdk wget unzip git
```

Verify Java installation:
```bash
java -version
# Expected: openjdk version "17.x.x"
```

### 1.2 Setup Android Commandline Tools & SDK
Set up the headless Android SDK directory:
```bash
export ANDROID_HOME="/opt/android-sdk"
export ANDROID_SDK_ROOT="/opt/android-sdk"
export PATH="${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools"
sudo mkdir -p ${ANDROID_HOME}/cmdline-tools

# Download latest Android command-line tools
sudo wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O /tmp/cmdline-tools.zip
sudo unzip -q /tmp/cmdline-tools.zip -d ${ANDROID_HOME}/cmdline-tools
sudo mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest
sudo rm /tmp/cmdline-tools.zip

# Grant ownership to current user
sudo chown -R $USER:$USER ${ANDROID_HOME}

# Accept licenses and install Android 34 build tools & platforms
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

### 1.3 Clone & Configure Project
```bash
git clone https://github.com/mdmehrabahmedsaif/DNS_Protector.git
cd DNS_Protector

# Copy environment variables
cp .env.example .env

# Create local.properties pointing to SDK
echo "sdk.dir=${ANDROID_HOME}" > local.properties
```

### 1.4 Build the Release APK
```bash
# Build release APK using Gradle
gradle assembleRelease --no-daemon
```
The compiled APK will be generated at:
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

---

## Target 2: Docker Container (Automated Isolated Build)

Use Docker to build the APK in a clean, reproducible container without installing Java or Android SDK on your host system.

### 2.1 Prerequisites
- [Docker Engine](https://docs.docker.com/engine/install/) (v20.10+)
- [Docker Compose](https://docs.docker.com/compose/) (v2.0+)

### 2.2 Build via Docker Compose
Run the following single command from the project root:
```bash
docker compose up --build
```

### 2.3 Build via Standard Docker CLI
Alternatively, build manually using the included `Dockerfile`:
```bash
# 1. Build the builder image
docker build -t dns-protector-builder .

# 2. Compile APK and copy output to host
docker run --rm -v $(pwd)/build-output:/workspace/app/build/outputs/apk dns-protector-builder
```
The resulting APK will be placed in the `./build-output/` directory on your host machine.

---

## Database Setup & Migrations

DNS Protector includes both an on-device lightweight state engine (powered by Android `SharedPreferences` for zero battery drain) and a relational SQL database schema for auditing, logs, and fleet policy management.

### 3.1 Initializing the Database Schema
The database schema creates tables for application configuration, active DNS profiles, security policies, and tamper attempt audit logs.

#### For SQLite:
```bash
sqlite3 dns_protector.db < database/schema.sql
```

#### For PostgreSQL / MySQL:
```bash
psql -U your_user -d dns_protector_db -f database/schema.sql
```

### 3.2 Seeding Demo & Default Data
Populate the database with pre-configured DNS filter endpoints (NextDNS, AdGuard Family, CleanBrowsing) and sample security audit logs:

#### For SQLite:
```bash
sqlite3 dns_protector.db < database/seed.sql
```

#### For PostgreSQL:
```bash
psql -U your_user -d dns_protector_db -f database/seed.sql
```

---

## First Admin Login & Security Activation

After installing the APK onto an Android device, complete the initial administrative setup to enable anti-tamper enforcement:

```
[Install APK] ──> [Open App] ──> [Enable Accessibility Service] ──> [Activate Device Admin] ──> [Protection Active (Green)]
```

### Step 1: Install APK to Device or Emulator
```bash
adb install -r app-release.apk
```

### Step 2: Launch the App
Open **DNS Protector** from the application launcher. On the first launch, the main status ring will show **PROTECTION INACTIVE** in red.

### Step 3: Enable the Accessibility Blocker Service
1. In the app dashboard, tap the green **"ENABLE"** button next to **Accessibility Service**.
2. Android will automatically redirect to `Settings > Accessibility > Downloaded apps`.
3. Tap **DNS Protector** and toggle the switch to **ON**.
4. Confirm the system security prompt.
5. Return to DNS Protector. The Accessibility indicator dot will turn solid green (**ACTIVE**).

### Step 4: Activate Device Administrator (Anti-Uninstall Lock)
1. In the app dashboard, tap the green **"ENABLE"** button next to **Device Administrator**.
2. The Android Device Administrator confirmation screen will appear.
3. Tap **"Activate this device admin app"**.
4. Return to the app. Both dots will now show green, and the header will display:
   ```
   PROTECTION ACTIVE
   ```

### Step 5: Test the Lock
1. Open phone **Settings** > **Network & Internet** > **Private DNS**.
2. Tap to modify the DNS setting.
3. DNS Protector will immediately detect the dialog `"Select Private DNS Mode"` and close it automatically, preserving the active DNS filter.

---

## Branding & Customization Guide

You can easily rebrand and tailor DNS Protector for your own organization or product lineup.

### 1. Change Application Name
Open `app/src/main/res/values/strings.xml`:
```xml
<string name="app_name">My Custom Guard</string>
```

### 2. Change Application Package Identifier (Application ID)
Open `app/build.gradle` and update the `applicationId` and `namespace`:
```groovy
defaultConfig {
    applicationId "com.mycompany.dnsguard"
    // ...
}
```

### 3. Change Application Icons & Logos
Replace the vector and drawable assets located in:
- `app/src/main/res/drawable/ic_logo_dns.xml` (Main launcher logo)
- `app/src/main/res/drawable/ic_shield_secure.xml` (Status shield asset)

### 4. Customise Theme & Status Colors
Open `app/src/main/res/layout/activity_main.xml` or create `app/src/main/res/values/colors.xml`:
```xml
<resources>
    <color name="status_active_green">#00E676</color>
    <color name="status_inactive_red">#FF5252</color>
    <color name="background_dark">#0F172A</color>
    <color name="card_background">#1E293B</color>
</resources>
```

---

## Troubleshooting & Verification

| Issue | Cause | Solution |
| :--- | :--- | :--- |
| `sdkmanager: command not found` | PATH variable missing Android tools | Run `export PATH="${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin"` |
| `Accessibility Service inactive after reboot` | Battery optimization killing service | Go to `Settings > Apps > DNS Protector > Battery` and set to **Unrestricted** |
| `Cannot uninstall app` | Device Admin is actively protecting the app | Open DNS Protector and tap **HALT** on the Device Administrator card first |
