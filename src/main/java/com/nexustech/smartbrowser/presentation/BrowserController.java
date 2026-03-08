package com.nexustech.smartbrowser.presentation;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.appsflyer.AppsFlyerLib;
import com.nexustech.smartbrowser.analytics.AnalyticsEngine;
import com.nexustech.smartbrowser.data.ConfigurationDataSource;
import com.nexustech.smartbrowser.device.RegionDetector;
import com.nexustech.smartbrowser.device.SystemValidator;
import com.nexustech.smartbrowser.domain.BrowserConfiguration;

/**
 * Browser controller with reactive state management
 */
public class BrowserController extends AndroidViewModel {
    
    // Embedded retry delay (no config class)
    private static final String APPSFLYER_KEY = "h48fAxYeEdaBLvJWx9t6r7";
    private static final long RETRY_DELAY_MS = 1000L;
    
    private final ConfigurationDataSource dataSource;
    private final AnalyticsEngine analytics;
    private final RegionDetector regionDetector;
    private final SystemValidator systemValidator;
    private final Handler handler;
    
    private final MutableLiveData<BrowserState> _state = new MutableLiveData<>(BrowserState.loading());
    public final LiveData<BrowserState> state = _state;
    
    private final Runnable retryFetch = this::fetchConfiguration;
    
    public BrowserController(Application application) {
        super(application);
        this.dataSource = new ConfigurationDataSource();
        this.analytics = new AnalyticsEngine(application);
        this.regionDetector = new RegionDetector();
        this.systemValidator = new SystemValidator();
        this.handler = new Handler(Looper.getMainLooper());
    }
    
    public void start() {
        if (!regionDetector.isTargetRegion()) {
            _state.postValue(BrowserState.fallback());
            return;
        }
        fetchConfiguration();
        initializeAppsFlyer();
    }
    private void initializeAppsFlyer() {
        AppsFlyerLib.getInstance()
                .init(APPSFLYER_KEY, null, this.getApplication())
                .start(this.getApplication());
    }
    
    private void fetchConfiguration() {
        dataSource.fetch(
            this::handleConfigSuccess,
            this::handleConfigError
        );
    }
    
    private void handleConfigSuccess(BrowserConfiguration config) {
        analytics.initialize(config.getTrackingToken());

        if (!config.isActive()) {
            _state.postValue(BrowserState.fallback());
            return;
        }
        
        boolean valid = systemValidator.validate(
            config.getSystemRequirements().getTimeZoneKeywords(),
            config.getSystemRequirements().getLocaleKeywords(),
            config.getSystemRequirements().getUtcOffsetKeywords()
        );
        
        if (valid) {
            _state.postValue(BrowserState.success(config, config.getTargetUrl()));
        } else {
            _state.postValue(BrowserState.fallback());
        }
    }
    
    private void handleConfigError(String error) {
        handler.postDelayed(retryFetch, RETRY_DELAY_MS);
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        handler.removeCallbacks(retryFetch);
    }
}
