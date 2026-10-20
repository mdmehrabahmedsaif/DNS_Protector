-- ==============================================================================
-- DNS Protector - Database Schema
-- Version: 1.0.0
-- Dialect: SQLite / PostgreSQL compatible
-- ==============================================================================

-- 1. App Configuration Table (Key-Value Store)
CREATE TABLE IF NOT EXISTS app_configuration (
    config_key VARCHAR(100) PRIMARY KEY,
    config_value TEXT NOT NULL,
    description TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Protected DNS Profiles
CREATE TABLE IF NOT EXISTS dns_profiles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    provider VARCHAR(100) NOT NULL,
    dot_host VARCHAR(255) NOT NULL,
    doh_url VARCHAR(255),
    ipv4_primary VARCHAR(45),
    ipv4_secondary VARCHAR(45),
    filter_category VARCHAR(50) DEFAULT 'Adult/Malware',
    is_active BOOLEAN DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Security & Anti-Tamper Policies
CREATE TABLE IF NOT EXISTS security_policies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    policy_name VARCHAR(100) NOT NULL UNIQUE,
    is_enabled BOOLEAN DEFAULT 1,
    target_package VARCHAR(255) NOT NULL,
    target_window_title VARCHAR(255) NOT NULL,
    enforcement_action VARCHAR(50) DEFAULT 'GLOBAL_ACTION_BACK',
    description TEXT
);

-- 4. Tamper Event Audit Logs
CREATE TABLE IF NOT EXISTS tamper_audit_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    source_package VARCHAR(255) NOT NULL,
    detected_dialog_title VARCHAR(255) NOT NULL,
    action_executed VARCHAR(100) NOT NULL,
    was_successful BOOLEAN DEFAULT 1,
    device_model VARCHAR(100),
    os_version VARCHAR(50)
);

-- 5. Admin Authentication & Recovery
CREATE TABLE IF NOT EXISTS admin_security (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    password_hash VARCHAR(255),
    salt VARCHAR(100),
    failed_attempts INTEGER DEFAULT 0,
    lockout_until TIMESTAMP NULL,
    device_admin_active BOOLEAN DEFAULT 0,
    accessibility_active BOOLEAN DEFAULT 0,
    last_verified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Indexes for Performance & Quick Lookups
CREATE INDEX IF NOT EXISTS idx_dns_profiles_active ON dns_profiles(is_active);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON tamper_audit_logs(event_timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_source ON tamper_audit_logs(source_package);
