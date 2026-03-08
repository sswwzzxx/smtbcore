package com.nexustech.smartbrowser.analytics;

import android.content.Context;
import android.util.Log;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.nexustech.smartbrowser.domain.TrackingEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * AppsFlyer SDK tracker implementation
 */
public class AppsFlyerEventTracker implements AnalyticsEngine.EventTracker {
    
    private static final String TAG = "AppsFlyerTracker";
    
    private final Context context;
    
    public AppsFlyerEventTracker(Context context) {
        this.context = context;
    }
    
    @Override
    public void initialize(String token) {
        // AppsFlyer initialization handled elsewhere
    }
    
    @Override
    public void track(String eventToken, String jsonData) {
        // Not used for AppsFlyer
    }
    
    @Override
    public void trackEvent(String eventName, Map<String, Object> data, TrackingEvent event) {
        Map<String, Object> params = new HashMap<>(data);
        
        // Add revenue parameters if present
        if (data.containsKey("amount") || data.containsKey("value")) {
            double amount = extractAmount(data);
            String currency = extractCurrency(data);
            params.put(AFInAppEventParameterName.REVENUE, String.valueOf(amount));
            params.put(AFInAppEventParameterName.CURRENCY, currency);
        }
        
        AppsFlyerLib.getInstance().logEvent(
            context,
            eventName,
            params,
            new AppsFlyerRequestListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Event success: " + eventName);
                }
                
                @Override
                public void onError(int code, String message) {
                    Log.e(TAG, "Event error: " + message);
                }
            }
        );
    }
    
    private double extractAmount(Map<String, Object> data) {
        Object amount = data.get("amount");
        if (amount == null) amount = data.get("value");
        try {
            return amount != null ? Double.parseDouble(amount.toString()) : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private String extractCurrency(Map<String, Object> data) {
        Object currency = data.get("currency");
        return currency != null ? currency.toString() : "USD";
    }
}
