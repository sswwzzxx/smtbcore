package com.nexustech.smartbrowser.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Functional request signer with stream API
 */
public class RequestSigner {
    
    // Embedded secret (no config class)
    private static final String APP_SECRET = "stycuyiu";
    
    public String sign(Map<String, String> params, long timestamp) {
        String paramString = new TreeMap<>(params) {{
            put("timestamp", String.valueOf(timestamp));
        }}
        .entrySet()
        .stream()
        .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
        .map(e -> e.getKey() + "=" + e.getValue())
        .collect(Collectors.joining("&", "", "&appSecret=" + APP_SECRET));
        
        return sha256(paramString);
    }
    
    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 failed", e);
        }
    }
}
