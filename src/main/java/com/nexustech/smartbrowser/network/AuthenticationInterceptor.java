package com.nexustech.smartbrowser.network;

import com.nexustech.smartbrowser.security.RequestSigner;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication interceptor with functional style
 */
public class AuthenticationInterceptor implements Interceptor {
    
    private final RequestSigner signer;
    
    public AuthenticationInterceptor() {
        this.signer = new RequestSigner();
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        
        Map<String, String> signParams = Optional.ofNullable(original.header("X-Sign-Params"))
            .map(this::parseParams)
            .orElse(new HashMap<>());
        
        long timestamp = System.currentTimeMillis();
        String signature = signer.sign(signParams, timestamp);
        
        Request authenticated = original.newBuilder()
            .removeHeader("X-Sign-Params")
            .addHeader("timestamp", String.valueOf(timestamp))
            .addHeader("sign", signature)
            .build();
        
        return chain.proceed(authenticated);
    }
    
    private Map<String, String> parseParams(String header) {
        Map<String, String> params = new HashMap<>();
        for (String pair : header.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                params.put(kv[0], kv[1]);
            }
        }
        return params;
    }
}
