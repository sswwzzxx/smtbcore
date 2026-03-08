package com.nexustech.smartbrowser.ui.bridge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nexustech.smartbrowser.core.R;
import com.nexustech.smartbrowser.domain.BrowserConfiguration;
import com.nexustech.smartbrowser.domain.TrackingEvent;
import com.nexustech.smartbrowser.security.CryptoEngine;
import com.nexustech.smartbrowser.analytics.AnalyticsEngine;
import com.nexustech.smartbrowser.ui.dialog.AlertDialogBuilder;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * JavaScript bridge for WebView communication
 * Refactored from JsBridge with command handler pattern
 */
public class JavaScriptBridge {
    
    private static final String TAG = "JavaScriptBridge";
    private static final String ENCRYPTED_WINDOW_EVENT = "almIgCEHh1SIejdy9aksZw==";
    
    private final Context context;
    private final BrowserConfiguration configuration;
    private final Activity activity;
    private final AnalyticsEngine analytics;
    private final BridgeCommandHandler commandHandler;
    private final Gson gson;
    
    public JavaScriptBridge(Context context, BrowserConfiguration configuration, 
                           Activity activity, AnalyticsEngine analytics) {
        this.context = context;
        this.configuration = configuration;
        this.activity = activity;
        this.analytics = analytics;
        this.commandHandler = new BridgeCommandHandler(context, activity);
        this.gson = new Gson();
    }
    
    @JavascriptInterface
    public void postMessage(String eventName, String data) {
        handleEvent("postMessage", eventName, data);
    }
    
    @JavascriptInterface
    public void openAndroid(String url) {
        commandHandler.openExternal(url);
    }
    
    @JavascriptInterface
    public void openWebView(String url) {
        // WebView navigation handled internally
        Log.d(TAG, "openWebView: " + url);
    }
    
    @JavascriptInterface
    public void closeWebView() {
        commandHandler.closeWebView();
    }
    
    @JavascriptInterface
    public void eventTracker(String eventType, String eventValues) {
        handleEvent("eventTracker", eventType, eventValues);
    }
    
    @JavascriptInterface
    public void onEvent(String data) {
        try {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> eventData = gson.fromJson(data, type);
            String eventName = eventData.get("eventName") != null 
                ? eventData.get("eventName").toString() : "";
            String eventValue = eventData.get("eventValue") != null 
                ? eventData.get("eventValue").toString() : "";
            handleEvent("onEvent", eventName, eventValue);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing onEvent data: " + data, e);
        }
    }
    
    @JavascriptInterface
    public void sendEvent(String eventName, String jsonStr) {
        Log.d(TAG, "sendEvent: " + eventName + ", data: " + jsonStr);
        analytics.track(eventName, jsonStr);
    }
    
    @JavascriptInterface
    public void openUrl(String url) {
        Log.d(TAG, "openUrl: " + url);
        commandHandler.openExternal(url);
    }
    
    private void handleEvent(String methodName, String eventName, String data) {
        Log.d(TAG, "Event from " + methodName + ": " + eventName + ", data: " + data);
        
        try {
            // Check for special window open event
            CryptoEngine crypto = new CryptoEngine();
            String windowOpenEvent = crypto.decrypt(ENCRYPTED_WINDOW_EVENT);
            if (eventName.equals(windowOpenEvent)) {
                handleWindowOpen(data);
                return;
            }
            
            // Process tracking event
            TrackingEvent event = analytics.processEvent(eventName, data, configuration)
                .orElse(null);
            
            if (event != null && event.shouldNavigate()) {
                showNavigationDialog(event);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing event: " + eventName, e);
        }
    }
    
    private void showNavigationDialog(TrackingEvent event) {
        activity.runOnUiThread(() -> {
            TrackingEvent.DialogSettings dialogSettings = event.getDialogSettings();
            TrackingEvent.NavigationAction navigationAction = event.getNavigationAction();
            
            String title = (dialogSettings != null && dialogSettings.getTitle() != null 
                && !dialogSettings.getTitle().trim().isEmpty())
                ? dialogSettings.getTitle()
                : context.getString(R.string.dialog_title_default);
            
            String okText = (dialogSettings != null && dialogSettings.getConfirmText() != null 
                && !dialogSettings.getConfirmText().trim().isEmpty())
                ? dialogSettings.getConfirmText()
                : context.getString(R.string.dialog_ok_default);
            
            String cancelText = (dialogSettings != null && dialogSettings.getCancelText() != null 
                && !dialogSettings.getCancelText().trim().isEmpty())
                ? dialogSettings.getCancelText()
                : context.getString(R.string.dialog_cancel_default);
            
            String content = navigationAction != null && navigationAction.getMessage() != null 
                ? navigationAction.getMessage() : "";
            
            String jumpUrl = navigationAction != null ? navigationAction.getTargetUrl() : null;
            
            AlertDialogBuilder.create(activity)
                .setTitle(title)
                .setContent(content)
                .setConfirmText(okText)
                .setCancelText(cancelText)
                .setOnConfirmListener(() -> {
                    if (jumpUrl != null && !jumpUrl.trim().isEmpty()) {
                        commandHandler.openExternal(jumpUrl);
                    }
                })
                .show();
        });
    }
    
    private void handleWindowOpen(String data) {
        try {
            WindowOpenData openData = gson.fromJson(data, WindowOpenData.class);
            if (openData.url != null) {
                commandHandler.openExternal(openData.url);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening window", e);
        }
    }
    
    private static class WindowOpenData {
        public Integer uid;
        public String phone;
        public String email;
        public String cid;
        public String domain;
        public String url;
    }
}
