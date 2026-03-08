package com.nexustech.smartbrowser.ui.webview;

import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;

/**
 * Manages WebView page load retry logic
 */
public class WebViewRetryManager {
    
    private static final long RETRY_DELAY_MS = 1000L;
    
    private final WebView webView;
    private final Handler handler;
    private Runnable retryRunnable;
    private boolean hasError = false;
    
    public WebViewRetryManager(WebView webView) {
        this.webView = webView;
        this.handler = new Handler(Looper.getMainLooper());
    }
    
    public void onPageStarted() {
        hasError = false;
    }
    
    public void onPageFinished() {
        if (!hasError) {
            cancelRetry();
        }
    }
    
    public void onPageError(String failingUrl) {
        hasError = true;
        scheduleRetry(failingUrl);
    }
    
    public void cleanup() {
        cancelRetry();
    }
    
    private void scheduleRetry(String url) {
        cancelRetry();
        retryRunnable = () -> {
            if (url != null) {
                webView.loadUrl(url);
            }
        };
        handler.postDelayed(retryRunnable, RETRY_DELAY_MS);
    }
    
    private void cancelRetry() {
        if (retryRunnable != null) {
            handler.removeCallbacks(retryRunnable);
            retryRunnable = null;
        }
    }
}
