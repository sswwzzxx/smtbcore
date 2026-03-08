package com.nexustech.smartbrowser.analytics;

import android.content.Context;
import android.util.Log;
import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nexustech.smartbrowser.domain.TrackingEvent;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Adjust SDK tracker implementation
 */
public class AdjustEventTracker implements AnalyticsEngine.EventTracker {
    
    private static final String TAG = "AdjustTracker";
    private static final String REVENUE_PARAM = "ad_revenue";
    private static final String CURRENCY_INR = "INR";
    
    private final Context context;
    private final Gson gson;
    
    public AdjustEventTracker(Context context) {
        this.context = context;
        this.gson = new Gson();
    }
    
    @Override
    public void initialize(String token) {
        if (token == null || token.isEmpty()) return;
        
        AdjustConfig config = new AdjustConfig(context, token, AdjustConfig.ENVIRONMENT_SANDBOX);
        config.enableSendingInBackground();
        config.setLogLevel(LogLevel.VERBOSE);
        Adjust.initSdk(config);
    }
    
    @Override
    public void track(String eventToken, String jsonData) {
        if (eventToken == null || jsonData == null) return;
        
        try {
            Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
            HashMap<String, Object> params = gson.fromJson(jsonData, type);
            
            AdjustEvent event = new AdjustEvent(eventToken);
            params.forEach((key, value) -> {
                if (REVENUE_PARAM.equalsIgnoreCase(key)) {
                    double revenue = parseDouble(value);
                    event.setRevenue(revenue, CURRENCY_INR);
                } else {
                    event.addCallbackParameter(key, value.toString());
                }
            });
            
            Adjust.trackEvent(event);
        } catch (Exception e) {
            Log.e(TAG, "Track error", e);
        }
    }
    
    @Override
    public void trackEvent(String eventName, Map<String, Object> data, TrackingEvent event) {
        AdjustEvent adjustEvent = new AdjustEvent(event.getEventToken());
        
        // Extract revenue if present
        double amount = extractAmount(data);
        String currency = extractCurrency(data);
        
        if (amount > 0) {
            adjustEvent.setRevenue(amount, currency);
        }
        
        Adjust.trackEvent(adjustEvent);
    }
    
    private double extractAmount(Map<String, Object> data) {
        return Optional.ofNullable(data.get("amount"))
            .or(() -> Optional.ofNullable(data.get("value")))
            .map(this::parseDouble)
            .orElse(0.0);
    }
    
    private String extractCurrency(Map<String, Object> data) {
        return Optional.ofNullable(data.get("currency"))
            .map(Object::toString)
            .orElse("USD");
    }
    
    private double parseDouble(Object value) {
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
