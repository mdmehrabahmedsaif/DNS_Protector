-- ==============================================================================
-- DNS Protector - Database Seed Data
-- Version: 1.0.0
-- Purpose: Pre-populates the database with default working rules, profiles, and demo logs
-- ==============================================================================

-- 1. Insert Initial App Configuration
INSERT INTO app_configuration (config_key, config_value, description) VALUES
('protection_enabled', 'true', 'Master toggle state for DNS enforcement'),
('auto_reorder_front_ms', '800', 'Delay in ms to bring DNS Protector back to front on tampering'),
('block_settings_package', 'com.android.settings', 'Android system settings package ID'),
('target_dialog_title', 'Select Private DNS Mode', 'Window title string intercepted by Accessibility Service'),
('strict_mode', 'true', 'Whether Device Admin uninstall protection is enforced');

-- 2. Insert Default Protected DNS Profiles
INSERT INTO dns_profiles (name, provider, dot_host, doh_url, ipv4_primary, ipv4_secondary, filter_category, is_active) VALUES
('NextDNS Adult & Tracker Blocker', 'NextDNS', 'custom-dns-id.dns.nextdns.io', 'https://dns.nextdns.io/custom-dns-id', '45.90.28.0', '45.90.30.0', 'Pornography, Malware & Tracking', 1),
('AdGuard Family Protection', 'AdGuard', 'family.adguard-dns.com', 'https://dns.adguard-dns.com/dns-query', '94.140.14.15', '94.140.15.16', 'Adult Websites & SafeSearch', 0),
('CleanBrowsing Adult Filter', 'CleanBrowsing', 'adult-filter-dns.cleanbrowsing.org', 'https://doh.cleanbrowsing.org/doh/adult-filter/', '185.228.168.10', '185.228.169.11', 'Adult Content & Explicit Domains', 0),
('Cloudflare 1.1.1.3 Family', 'Cloudflare', 'family.cloudflare-dns.com', 'https://family.cloudflare-dns.com/dns-query', '1.1.1.3', '1.0.0.3', 'Malware and Adult Content', 0);

-- 3. Insert Core Security Policy
INSERT INTO security_policies (policy_name, is_enabled, target_package, target_window_title, enforcement_action, description) VALUES
('Block Private DNS Popup', 1, 'com.android.settings', 'Select Private DNS Mode', 'GLOBAL_ACTION_BACK', 'Instantly fires a system back action when user attempts to open Private DNS options'),
('Prevent Package Uninstall', 1, 'com.android.settings', 'Uninstall app', 'DEVICE_ADMIN_POLICY', 'Utilizes Device Policy Manager to restrict uninstallation attempts');

-- 4. Insert Demo Tamper Audit Logs (Demonstrates active interception in real-time)
INSERT INTO tamper_audit_logs (event_timestamp, source_package, detected_dialog_title, action_executed, was_successful, device_model, os_version) VALUES
(DATETIME('now', '-2 hours'), 'com.android.settings', 'Select Private DNS Mode', 'GLOBAL_ACTION_BACK', 1, 'Pixel 7', 'Android 14'),
(DATETIME('now', '-1 hours'), 'com.android.settings', 'Select Private DNS Mode', 'GLOBAL_ACTION_BACK', 1, 'Pixel 7', 'Android 14'),
(DATETIME('now', '-15 minutes'), 'com.android.settings', 'Select Private DNS Mode', 'GLOBAL_ACTION_BACK', 1, 'Pixel 7', 'Android 14');

-- 5. Insert Initial Security State
INSERT INTO admin_security (password_hash, salt, failed_attempts, device_admin_active, accessibility_active) VALUES
(NULL, 'salt_demo_dns_protector', 0, 1, 1);
