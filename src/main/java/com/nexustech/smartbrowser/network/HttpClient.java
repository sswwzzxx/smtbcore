package com.nexustech.smartbrowser.network;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Functional HTTP client with fluent API
 */
public class HttpClient {
    
    private final OkHttpClient client;
    private final Map<String, String> defaultHeaders;
    
    private HttpClient(Builder builder) {
        this.client = new OkHttpClient.Builder()
            .connectTimeout(builder.connectTimeout, TimeUnit.SECONDS)
            .readTimeout(builder.readTimeout, TimeUnit.SECONDS)
            .writeTimeout(builder.writeTimeout, TimeUnit.SECONDS)
            .addInterceptor(builder.interceptor)
            .build();
        this.defaultHeaders = builder.headers;
    }
    
    public RequestBuilder get(String url) {
        return new RequestBuilder(this, url);
    }
    
    public static class Builder {
        private long connectTimeout = 15;
        private long readTimeout = 15;
        private long writeTimeout = 15;
        private okhttp3.Interceptor interceptor;
        private Map<String, String> headers = new HashMap<>();
        
        public Builder timeout(long seconds) {
            this.connectTimeout = seconds;
            this.readTimeout = seconds;
            this.writeTimeout = seconds;
            return this;
        }
        
        public Builder interceptor(okhttp3.Interceptor interceptor) {
            this.interceptor = interceptor;
            return this;
        }
        
        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }
        
        public HttpClient build() {
            return new HttpClient(this);
        }
    }
    
    public static class RequestBuilder {
        private final HttpClient httpClient;
        private final String url;
        private final Map<String, String> headers = new HashMap<>();
        
        RequestBuilder(HttpClient httpClient, String url) {
            this.httpClient = httpClient;
            this.url = url;
            this.headers.putAll(httpClient.defaultHeaders);
        }
        
        public RequestBuilder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }
        
        public void execute(Consumer<Result> onResult) {
            Request.Builder requestBuilder = new Request.Builder().url(url);
            headers.forEach(requestBuilder::addHeader);
            
            httpClient.client.newCall(requestBuilder.build()).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onResult.accept(Result.failure(e.getMessage()));
                }
                
                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            onResult.accept(Result.success(response.body().string()));
                        } else {
                            onResult.accept(Result.failure("HTTP " + response.code()));
                        }
                    } catch (IOException e) {
                        onResult.accept(Result.failure(e.getMessage()));
                    }
                }
            });
        }
    }
    
    public static class Result {
        private final String data;
        private final String error;
        private final boolean success;
        
        private Result(String data, String error, boolean success) {
            this.data = data;
            this.error = error;
            this.success = success;
        }
        
        public static Result success(String data) {
            return new Result(data, null, true);
        }
        
        public static Result failure(String error) {
            return new Result(null, error, false);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getData() {
            return data;
        }
        
        public String getError() {
            return error;
        }
        
        public Result onSuccess(Consumer<String> consumer) {
            if (success) consumer.accept(data);
            return this;
        }
        
        public Result onFailure(Consumer<String> consumer) {
            if (!success) consumer.accept(error);
            return this;
        }
    }
}
