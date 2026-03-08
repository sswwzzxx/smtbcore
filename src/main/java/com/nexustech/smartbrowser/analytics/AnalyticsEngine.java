package com.nexustech.smartbrowser.analytics;

import android.content.Context;
import com.nexustech.smartbrowser.domain.BrowserConfiguration;
import com.nexustech.smartbrowser.domain.TrackingEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Analytics engine with strategy pattern
 */
public class AnalyticsEngine {
    
    // Embedded SDK key (no config class)
    private final Map<String, EventTracker> trackers;
    private final EventRouter router;
    
    public AnalyticsEngine(Context context) {
        this.trackers = new HashMap<>();
        this.trackers.put("adjust", new AdjustEventTracker(context));
        this.trackers.put("appsflyer", new AppsFlyerEventTracker(context));
        this.router = new EventRouter();
    }
    
    public void initialize(String adjustToken) {
        trackers.values().forEach(tracker -> tracker.initialize(adjustToken));
    }
    
    public Optional<TrackingEvent> processEvent(String eventName, String eventData, BrowserConfiguration config) {
        return router.route(eventName, eventData, config, this::trackWithAllProviders);
    }
    
    public void track(String eventToken, String jsonData) {
        trackers.values().forEach(tracker -> tracker.track(eventToken, jsonData));
    }
    
    private void trackWithAllProviders(String eventName, Map<String, Object> data, TrackingEvent event) {
        trackers.values().forEach(tracker -> tracker.trackEvent(eventName, data, event));
    }
    
    /**
     * Event tracker interface
     */
    interface EventTracker {
        void initialize(String token);
        void track(String eventToken, String jsonData);
        void trackEvent(String eventName, Map<String, Object> data, TrackingEvent event);
    }
}
