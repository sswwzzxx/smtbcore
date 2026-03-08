package com.nexustech.smartbrowser.presentation;

import com.nexustech.smartbrowser.domain.BrowserConfiguration;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Immutable browser state with functional updates
 */
public class BrowserState {
    
    private final Status status;
    private final BrowserConfiguration configuration;
    private final String url;
    private final String error;
    
    private BrowserState(Status status, BrowserConfiguration configuration, String url, String error) {
        this.status = status;
        this.configuration = configuration;
        this.url = url;
        this.error = error;
    }
    
    public static BrowserState loading() {
        return new BrowserState(Status.LOADING, null, null, null);
    }
    
    public static BrowserState success(BrowserConfiguration config, String url) {
        return new BrowserState(Status.SUCCESS, config, url, null);
    }
    
    public static BrowserState error(String error) {
        return new BrowserState(Status.ERROR, null, null, error);
    }
    
    public static BrowserState fallback() {
        return new BrowserState(Status.FALLBACK, null, null, null);
    }
    
    public void handle(
        Runnable onLoading,
        Consumer<NavigationData> onSuccess,
        Consumer<String> onError,
        Runnable onFallback
    ) {
        switch (status) {
            case LOADING:
                onLoading.run();
                break;
            case SUCCESS:
                onSuccess.accept(new NavigationData(url, configuration));
                break;
            case ERROR:
                onError.accept(error);
                break;
            case FALLBACK:
                onFallback.run();
                break;
        }
    }
    
    public Optional<NavigationData> getNavigationData() {
        return status == Status.SUCCESS 
            ? Optional.of(new NavigationData(url, configuration))
            : Optional.empty();
    }
    
    public enum Status {
        LOADING, SUCCESS, ERROR, FALLBACK
    }
    
    public static class NavigationData {
        public final String url;
        public final BrowserConfiguration configuration;
        
        NavigationData(String url, BrowserConfiguration configuration) {
            this.url = url;
            this.configuration = configuration;
        }
    }
}
