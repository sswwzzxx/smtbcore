package com.nexustech.smartbrowser.device;

import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Functional user agent factory
 */
public class UserAgentFactory {
    
    private final Context context;
    private final UnaryOperator<String> sanitizer;
    
    public UserAgentFactory(Context context) {
        this.context = context;
        this.sanitizer = this::sanitize;
    }
    
    public String create(String appVersion, String deviceId) {
        return Optional.ofNullable(getBaseUserAgent())
            .map(sanitizer)
            .map(ua -> ua.replace("; wv", "; xx-xx"))
            .map(ua -> appendMetadata(ua, appVersion, deviceId))
            .orElseThrow(() -> new IllegalStateException("Failed to create user agent"));
    }
    
    private String getBaseUserAgent() {
        try {
            return WebSettings.getDefaultUserAgent(context);
        } catch (Exception e) {
            return System.getProperty("http.agent");
        }
    }
    
    private String sanitize(String ua) {
        return ua.chars()
            .mapToObj(c -> (c <= 0x1f || c >= 0x7f) 
                ? String.format("\\u%04x", c) 
                : String.valueOf((char) c))
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();
    }
    
    private String appendMetadata(String ua, String version, String deviceId) {
        StringBuilder builder = new StringBuilder(ua)
            .append("/").append(Build.BRAND);
        
        if (version != null && !version.isEmpty()) {
            builder.append(" AppShellVer:").append(version);
        }
        
        return builder.append(" UUID/").append(deviceId).toString();
    }
}
