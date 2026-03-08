package com.nexustech.smartbrowser.analytics;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nexustech.smartbrowser.domain.BrowserConfiguration;
import com.nexustech.smartbrowser.domain.TrackingEvent;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Routes events to appropriate handlers
 */
public class EventRouter {
    
    private final Gson gson = new Gson();
    
    public Optional<TrackingEvent> route(String eventName, String eventData, 
                                         BrowserConfiguration config,
                                         TriConsumer<String, Map<String, Object>, TrackingEvent> handler) {
        if (config == null) return Optional.empty();
        
        return findEvent(config.getRevenueEvents(), eventName)
            .or(() -> findEvent(config.getStandardEvents(), eventName))
            .map(event -> {
                Map<String, Object> data = parseEventData(eventData);
                handler.accept(eventName, data, event);
                return event.shouldNavigate() ? event : null;
            });
    }
    
    private Optional<TrackingEvent> findEvent(List<TrackingEvent> events, String eventName) {
        if (events == null) return Optional.empty();
        return events.stream()
            .filter(e -> eventName.equals(e.getEventName()))
            .findFirst();
    }
    
    private Map<String, Object> parseEventData(String jsonData) {
        try {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            return gson.fromJson(jsonData, type);
        } catch (Exception e) {
            return Map.of();
        }
    }
    
    @FunctionalInterface
    interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }
}
