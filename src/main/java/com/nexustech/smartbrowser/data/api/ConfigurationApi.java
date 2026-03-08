package com.nexustech.smartbrowser.data.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * API service for configuration endpoints
 */
public interface ConfigurationApi {
    
    @GET("jvdfbn/sghytufygiuy/privacyPolicy")
    Call<String> fetchConfiguration(@Header("X-Sign-Params") String signatureParams);
}
