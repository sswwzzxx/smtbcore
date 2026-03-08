package com.nexustech.smartbrowser.ui.webview;

import android.annotation.SuppressLint;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.nexustech.smartbrowser.device.UserAgentFactory;
import com.nexustech.smartbrowser.domain.BrowserConfiguration;
import java.util.UUID;

/**
 * Configures WebView settings
 */
public class WebViewConfigurator {
    
    private final UserAgentFactory uaFactory;
    
    public WebViewConfigurator(UserAgentFactory uaFactory) {
        this.uaFactory = uaFactory;
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    public void configure(WebView webView, BrowserConfiguration config) {
        WebSettings settings = webView.getSettings();
        
        // Enable JavaScript and DOM storage
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportMultipleWindows(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        
        // Configure viewport
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        
        // Disable zoom
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(false);
        
        // Clear cache
        webView.clearCache(true);
        
        // Configure User Agent
        configureUserAgent(settings, config);
    }
    
    private void configureUserAgent(WebSettings settings, BrowserConfiguration config) {
        if (config == null) {
            String uuid = UUID.randomUUID().toString();
            String userAgent = uaFactory.create("1.0.0", uuid);
            settings.setUserAgentString(userAgent);
        } else if (config.getUserAgentSettings().isCustomizeEnabled()) {
            String uuid = UUID.randomUUID().toString();
            String userAgent = uaFactory.create(
                config.getUserAgentSettings().getAppVersion(),
                uuid
            );
            settings.setUserAgentString(userAgent);
        }
    }
}
