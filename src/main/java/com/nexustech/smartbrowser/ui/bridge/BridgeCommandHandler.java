package com.nexustech.smartbrowser.ui.bridge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Handles commands from JavaScript bridge
 */
public class BridgeCommandHandler {
    
    private static final String TAG = "BridgeCommandHandler";
    
    private final Context context;
    private final Activity activity;
    
    public BridgeCommandHandler(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }
    
    public void openExternal(String url) {
        if (url == null || url.trim().isEmpty()) {
            return;
        }
        
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening URL: " + url, e);
            openSystemBrowser(url);
        }
    }
    
    public void closeWebView() {
        Log.d(TAG, "Closing WebView");
        activity.runOnUiThread(() -> {
            if (!activity.isTaskRoot()) {
                activity.finish();
            }
        });
    }
    
    private void openSystemBrowser(String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setComponent(null);
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open system browser", e);
        }
    }
}
