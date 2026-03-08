package com.nexustech.smartbrowser.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nexustech.smartbrowser.data.dto.ConfigurationDto;
import com.nexustech.smartbrowser.data.mapper.ConfigurationMapper;
import com.nexustech.smartbrowser.domain.BrowserConfiguration;
import com.nexustech.smartbrowser.network.AuthenticationInterceptor;
import com.nexustech.smartbrowser.network.HttpClient;
import com.nexustech.smartbrowser.security.CryptoEngine;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;

/**
 * Configuration data source with reactive callbacks
 */
public class ConfigurationDataSource {
    
    // Embedded configuration (no config class)
    private static final String ENCRYPTED_BASE_URL = "QZZ1qiZREkoQE3vLCk1Rs/++Dm0C1UqOm3X0lhFaY53zipjhWXHSkaj0nt/sxoDN";
    private static final String API_PATH = "jvdfbn/sghytufygiuy/privacyPolicy";
    private static final String SIGN_PARAMS = "urlPrefix=jvdfbn&appName=sghytufygiuy";
    
    private final HttpClient httpClient;
    private final CryptoEngine crypto;
    private final ConfigurationMapper mapper;
    private final Gson gson;
    
    public ConfigurationDataSource() {
        this.crypto = new CryptoEngine();
        this.httpClient = new HttpClient.Builder()
            .timeout(15)
            .interceptor(new AuthenticationInterceptor())
            .build();
        this.mapper = new ConfigurationMapper();
        this.gson = new Gson();
    }
    
    public void fetch(Consumer<BrowserConfiguration> onSuccess, Consumer<String> onError) {
        String baseUrl = crypto.decrypt(ENCRYPTED_BASE_URL);
        if (baseUrl == null) {
            onError.accept("Failed to decrypt base URL");
            return;
        }
        
        httpClient.get(baseUrl + API_PATH)
            .header("X-Sign-Params", SIGN_PARAMS)
            .execute(result -> result
                .onSuccess(encrypted -> processResponse(encrypted, onSuccess, onError))
                .onFailure(onError)
            );
    }
    
    private void processResponse(String encrypted, Consumer<BrowserConfiguration> onSuccess, Consumer<String> onError) {
        try {
            String decrypted = crypto.decrypt(encrypted);
            if (decrypted == null) {
                onError.accept("Decryption failed");
                return;
            }
            
            Type listType = new TypeToken<List<ConfigurationDto>>(){}.getType();
            List<ConfigurationDto> dtoList = gson.fromJson(decrypted, listType);
            
            if (dtoList != null && !dtoList.isEmpty()) {
                BrowserConfiguration config = mapper.toDomain(dtoList.get(0));
                onSuccess.accept(config);
            } else {
                onError.accept("Empty configuration");
            }
        } catch (Exception e) {
            onError.accept("Parse error: " + e.getMessage());
        }
    }
}
