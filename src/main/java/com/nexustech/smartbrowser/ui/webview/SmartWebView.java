package com.nexustech.smartbrowser.ui.webview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.appcompat.app.AppCompatActivity;
import com.nexustech.smartbrowser.core.R;
import com.nexustech.smartbrowser.device.UserAgentFactory;
import com.nexustech.smartbrowser.domain.BrowserConfiguration;
import com.nexustech.smartbrowser.security.CryptoEngine;
import com.nexustech.smartbrowser.analytics.AnalyticsEngine;
import com.nexustech.smartbrowser.ui.bridge.JavaScriptBridge;

/**
 * Smart WebView with enhanced features
 * Refactored from CustomWebView - functionality split into specialized managers
 */
public class SmartWebView extends FrameLayout implements FloatingButtonManager.FloatingButtonCallbacks {
    
    private WebView webView;
    private BrowserConfiguration configuration;
    private String initialUrl;
    private AppCompatActivity activity;
    private OnPageLoadCallback pageLoadCallback;
    
    // Managers
    private WebViewConfigurator configurator;
    private FileChooserHandler fileChooserHandler;
    private WebViewRetryManager retryManager;
    private FloatingButtonManager floatingButtonManager;
    
    private boolean isInitialized = false;
    
    public interface OnPageLoadCallback {
        void onPageLoaded(String url);
    }
    
    public SmartWebView(Context context) {
        this(context, null);
    }
    
    public SmartWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public SmartWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeWebView();
    }
    
    private void initializeWebView() {
        webView = new WebView(getContext());
        LayoutParams layoutParams = new LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        );
        addView(webView, layoutParams);
    }
    
    public void setup(AppCompatActivity activity, 
                     BrowserConfiguration config, 
                     String url,
                     ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher,
                     OnPageLoadCallback callback) {
        
        if (isInitialized) {
            reset();
        }
        
        this.activity = activity;
        this.configuration = config;
        this.initialUrl = url;
        this.pageLoadCallback = callback;
        
        // Initialize managers
        UserAgentFactory uaFactory = new UserAgentFactory(getContext());
        this.configurator = new WebViewConfigurator(uaFactory);
        this.fileChooserHandler = new FileChooserHandler(getContext());
        this.retryManager = new WebViewRetryManager(webView);
        
        fileChooserHandler.setPickMediaLauncher(pickMediaLauncher);
        
        // Configure WebView
        configurator.configure(webView, config);
        setupWebViewClients(activity);
        
        // Load URL
        webView.loadUrl(url);
        
        // Show floating button if needed
        if (config != null && config.getUiSettings().isFloatingButtonVisible()) {
            showFloatingButton();
        }
        
        isInitialized = true;
    }
    
    private void setupWebViewClients(AppCompatActivity activity) {
        webView.setWebViewClient(new SmartWebViewClient());
        webView.setWebChromeClient(new SmartWebChromeClient());
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            openExternalUrl(url);
        });
        
        // Add JavaScript bridge
        String bridgeName = configuration != null && configuration.getBridgeName() != null
            ? configuration.getBridgeName()
            : new CryptoEngine().decrypt("dZp4G3+flo7bhMY0/5zA6w==");
        
        if (bridgeName == null) {
            bridgeName = "jsBridge";
        }
        
        AnalyticsEngine analytics = new AnalyticsEngine(getContext());
        JavaScriptBridge jsBridge = new JavaScriptBridge(
            getContext(),
            configuration,
            activity,
            analytics
        );
        
        webView.addJavascriptInterface(jsBridge, bridgeName);
    }
    
    public boolean navigateBack() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }
    
    public void reload() {
        webView.reload();
    }
    
    public void loadInitialUrl() {
        if (initialUrl != null) {
            webView.loadUrl(initialUrl);
        }
    }
    
    public void handlePickMediaResult(Uri uri) {
        fileChooserHandler.handlePickMediaResult(uri);
    }
    
    public void cleanup() {
        retryManager.cleanup();
        fileChooserHandler.cleanup();
        if (floatingButtonManager != null) {
            floatingButtonManager.hide();
        }
    }
    
    private void showFloatingButton() {
        floatingButtonManager = new FloatingButtonManager(getContext(), this, this);
        floatingButtonManager.show();
    }
    
    private void reset() {
        cleanup();
        
        webView.stopLoading();
        webView.clearHistory();
        webView.clearCache(true);
        webView.clearFormData();
        
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        
        removeView(webView);
        webView.removeAllViews();
        webView.destroy();
        
        initializeWebView();
        
        isInitialized = false;
    }
    
    private void openExternalUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void openSystemBrowser(Uri uri) {
        try {
            Intent intent = Intent.parseUri(uri.toString(), Intent.URI_INTENT_SCHEME);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setComponent(null);
            getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // FloatingButtonCallbacks implementation
    @Override
    public void onBackPressed() {
        navigateBack();
    }
    
    @Override
    public void onReloadPressed() {
        reload();
    }
    
    @Override
    public void onHomePressed() {
        loadInitialUrl();
    }
    
    /**
     * Custom WebViewClient
     */
    private class SmartWebViewClient extends WebViewClient {
        
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            retryManager.onPageStarted();
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            retryManager.onPageFinished();
            if (pageLoadCallback != null) {
                pageLoadCallback.onPageLoaded(url);
            }
        }
        
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            retryManager.onPageError(failingUrl);
        }
        
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return handleUrlOverride(request.getUrl().toString());
        }
        
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return handleUrlOverride(url);
        }
        
        private boolean handleUrlOverride(String url) {
            if (url == null || url.startsWith("http://") || url.startsWith("https://")) {
                return false;
            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getContext().startActivity(intent);
                return true;
            } catch (Exception e) {
                openSystemBrowser(Uri.parse(url));
                e.printStackTrace();
                return true;
            }
        }
    }
    
    /**
     * Custom WebChromeClient
     */
    private class SmartWebChromeClient extends WebChromeClient {
        
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                       FileChooserParams fileChooserParams) {
            fileChooserHandler.handleFileChooser(filePathCallback);
            return true;
        }
        
        @Override
        public void onPermissionRequest(PermissionRequest request) {
            fileChooserHandler.handlePermissionRequest(request);
        }
        
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult jsResult) {
            new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
            jsResult.cancel();
            return true;
        }
        
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView newWebView = new WebView(getContext());
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();
            
            newWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    openExternalUrl(url);
                    return true;
                }
            });
            return true;
        }
    }
}
