package com.dns.protector;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

public class PrivateDnsBlockerService extends AccessibilityService {
    private static final String TAG = "PrivateDnsBlocker";
    private static final String SETTINGS_PACKAGE = "com.android.settings";
    private static final String DNS_DIALOG_TITLE = "Select Private DNS Mode";

    private static PrivateDnsBlockerService sInstance = null;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Accessibility Service Connected");
        sInstance = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Accessibility Service Destroyed");
        sInstance = null;
    }

    public static void disableService() {
        if (sInstance != null) {
            Log.i(TAG, "Disabling accessibility service programmatically");
            sInstance.disableSelf();
            sInstance = null;
        } else {
            Log.d(TAG, "disableService called but sInstance is null");
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;

        // Check if blocking feature is enabled in settings
        android.content.SharedPreferences prefs = getSharedPreferences("dns_protector_prefs", MODE_PRIVATE);
        boolean isBlockingEnabled = prefs.getBoolean("blocking_enabled", true);
        if (!isBlockingEnabled) {
            return;
        }

        // Check package name
        CharSequence packageName = event.getPackageName();
        if (packageName == null || !SETTINGS_PACKAGE.equals(packageName.toString())) {
            return;
        }

        // Fast Check: Check event text directly
        List<CharSequence> eventTexts = event.getText();
        if (eventTexts != null) {
            for (CharSequence txt : eventTexts) {
                if (txt != null && txt.toString().trim().equalsIgnoreCase(DNS_DIALOG_TITLE)) {
                    blockAccess();
                    return;
                }
            }
        }

        // Deep Check: Scan the node tree
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            try {
                findAndBlockDnsPopup(rootNode);
            } finally {
                rootNode.recycle();
            }
        }
    }

    private boolean findAndBlockDnsPopup(AccessibilityNodeInfo node) {
        if (node == null) return false;

        // Check if this node has the target title text
        CharSequence text = node.getText();
        if (text != null && text.toString().trim().equalsIgnoreCase(DNS_DIALOG_TITLE)) {
            blockAccess();
            return true;
        }

        // Recursively check children
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                if (findAndBlockDnsPopup(child)) {
                    child.recycle();
                    return true;
                }
                child.recycle();
            }
        }
        return false;
    }

    private void blockAccess() {
        Log.i(TAG, "Blocking Private DNS settings popup!");
        
        // Perform Global Back Action to close the dialog/popup immediately
        performGlobalAction(GLOBAL_ACTION_BACK);
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service Interrupted");
    }
}
