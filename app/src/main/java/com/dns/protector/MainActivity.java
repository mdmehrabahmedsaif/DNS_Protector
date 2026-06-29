package com.dns.protector;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.CompoundButton;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private View statusRing;
    private View statusDot;
    private TextView tvProtectionTitle;

    private TextView accessibilityStatusText;
    private View accessibilityIndicatorDot;
    private Button btnAccessibilityHalt;

    private TextView adminStatusText;
    private View adminIndicatorDot;
    private Button btnAdminHalt;

    private Switch switchBlocker;

    private DevicePolicyManager dpm;
    private ComponentName adminComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusRing = findViewById(R.id.status_ring);
        statusDot = findViewById(R.id.status_dot);
        tvProtectionTitle = findViewById(R.id.tv_protection_title);

        accessibilityStatusText = findViewById(R.id.accessibility_status_text);
        accessibilityIndicatorDot = findViewById(R.id.accessibility_indicator_dot);
        btnAccessibilityHalt = findViewById(R.id.btn_accessibility_halt);

        adminStatusText = findViewById(R.id.admin_status_text);
        adminIndicatorDot = findViewById(R.id.admin_indicator_dot);
        btnAdminHalt = findViewById(R.id.btn_admin_halt);

        switchBlocker = findViewById(R.id.switch_blocker);

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, DNSProtectorAdminReceiver.class);

        // Bind Accessibility Halt/Enable button
        btnAccessibilityHalt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean active = isAccessibilityServiceEnabled(MainActivity.this, PrivateDnsBlockerService.class);
                if (active) {
                    PrivateDnsBlockerService.disableService();
                    refreshUIDelayed();
                    bringAppToFrontDelayed();
                } else {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                }
            }
        });

        // Bind Device Admin Halt/Enable button
        btnAdminHalt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean active = dpm.isAdminActive(adminComponent);
                if (active) {
                    dpm.removeActiveAdmin(adminComponent);
                    refreshUIDelayed();
                    bringAppToFrontDelayed();
                } else {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, 
                            "DNS Protector requires Device Admin privileges to block uninstallation.");
                    startActivity(intent);
                }
            }
        });

        // Bind DNS Blocker protection Switch
        SharedPreferences prefs = getSharedPreferences("dns_protector_prefs", MODE_PRIVATE);
        boolean blockerEnabled = prefs.getBoolean("blocking_enabled", true);
        switchBlocker.setChecked(blockerEnabled);
        switchBlocker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharedPreferences("dns_protector_prefs", MODE_PRIVATE)
                        .edit()
                        .putBoolean("blocking_enabled", isChecked)
                        .apply();
                updateUI();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void refreshUIDelayed() {
        // Post a small delay for UI refresh to let Android broadcast receiver or status register update
        statusRing.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        }, 300);
    }

    private void updateUI() {
        boolean adminActive = dpm.isAdminActive(adminComponent);
        boolean accessActive = isAccessibilityServiceEnabled(this, PrivateDnsBlockerService.class);

        SharedPreferences prefs = getSharedPreferences("dns_protector_prefs", MODE_PRIVATE);
        boolean blockerEnabled = prefs.getBoolean("blocking_enabled", true);

        // Update Accessibility Row
        if (accessActive) {
            accessibilityStatusText.setText("ACTIVE");
            accessibilityStatusText.setTextColor(0xFF00E676); // Green
            accessibilityIndicatorDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF00E676));
            btnAccessibilityHalt.setText("HALT");
            btnAccessibilityHalt.setTextColor(0xFFFF5252); // Red Text
            btnAccessibilityHalt.setBackgroundResource(R.drawable.btn_border_red);
        } else {
            accessibilityStatusText.setText("INACTIVE");
            accessibilityStatusText.setTextColor(0xFFFF5252); // Red
            accessibilityIndicatorDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF5252));
            btnAccessibilityHalt.setText("ENABLE");
            btnAccessibilityHalt.setTextColor(0xFF00E676); // Green Text
            btnAccessibilityHalt.setBackgroundResource(R.drawable.btn_border_green);
        }

        // Update Device Admin Row
        if (adminActive) {
            adminStatusText.setText("ACTIVE");
            adminStatusText.setTextColor(0xFF00E676); // Green
            adminIndicatorDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF00E676));
            btnAdminHalt.setText("HALT");
            btnAdminHalt.setTextColor(0xFFFF5252); // Red Text
            btnAdminHalt.setBackgroundResource(R.drawable.btn_border_red);
        } else {
            adminStatusText.setText("INACTIVE");
            adminStatusText.setTextColor(0xFFFF5252); // Red
            adminIndicatorDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF5252));
            btnAdminHalt.setText("ENABLE");
            btnAdminHalt.setTextColor(0xFF00E676); // Green Text
            btnAdminHalt.setBackgroundResource(R.drawable.btn_border_green);
        }

        // Update Overall Protection status
        boolean protectionActive = adminActive && accessActive && blockerEnabled;
        if (protectionActive) {
            tvProtectionTitle.setText("PROTECTION ACTIVE");
            tvProtectionTitle.setTextColor(0xFF00E676); // Green
            statusRing.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x1F00E676)); // Light Translucent Green
            statusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF00E676)); // Solid Green
        } else {
            tvProtectionTitle.setText("PROTECTION INACTIVE");
            tvProtectionTitle.setTextColor(0xFFFF5252); // Red
            statusRing.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x1FFF5252)); // Light Translucent Red
            statusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF5252)); // Solid Red
        }
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> serviceClass) {
        String prefString = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (prefString != null) {
            TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
            splitter.setString(prefString);
            String serviceName = context.getPackageName() + "/" + serviceClass.getName();
            while (splitter.hasNext()) {
                String accessibilityService = splitter.next();
                if (accessibilityService.equalsIgnoreCase(serviceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void bringAppToFrontDelayed() {
        statusRing.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 800); // 800ms delay to override any system setting redirects
    }
}
