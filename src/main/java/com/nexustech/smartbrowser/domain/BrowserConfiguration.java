package com.nexustech.smartbrowser.domain;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/**
 * Main browser configuration model
 */
public class BrowserConfiguration implements Parcelable {
    private final String targetUrl;
    private final String trackingToken;
    private final String activationStatus;
    private final UserAgentSettings userAgentSettings;
    private final UISettings uiSettings;
    private final String bridgeName;
    private final List<TrackingEvent> revenueEvents;
    private final List<TrackingEvent> standardEvents;
    private final SystemRequirements systemRequirements;
    
    public BrowserConfiguration(String targetUrl, String trackingToken, String activationStatus,
                                UserAgentSettings userAgentSettings, UISettings uiSettings,
                                String bridgeName, List<TrackingEvent> revenueEvents,
                                List<TrackingEvent> standardEvents, SystemRequirements systemRequirements) {
        this.targetUrl = targetUrl;
        this.trackingToken = trackingToken;
        this.activationStatus = activationStatus;
        this.userAgentSettings = userAgentSettings;
        this.uiSettings = uiSettings;
        this.bridgeName = bridgeName;
        this.revenueEvents = revenueEvents;
        this.standardEvents = standardEvents;
        this.systemRequirements = systemRequirements;
    }
    
    protected BrowserConfiguration(Parcel in) {
        targetUrl = in.readString();
        trackingToken = in.readString();
        activationStatus = in.readString();
        userAgentSettings = in.readParcelable(UserAgentSettings.class.getClassLoader());
        uiSettings = in.readParcelable(UISettings.class.getClassLoader());
        bridgeName = in.readString();
        revenueEvents = in.createTypedArrayList(TrackingEvent.CREATOR);
        standardEvents = in.createTypedArrayList(TrackingEvent.CREATOR);
        systemRequirements = in.readParcelable(SystemRequirements.class.getClassLoader());
    }
    
    public static final Creator<BrowserConfiguration> CREATOR = new Creator<BrowserConfiguration>() {
        @Override
        public BrowserConfiguration createFromParcel(Parcel in) {
            return new BrowserConfiguration(in);
        }
        
        @Override
        public BrowserConfiguration[] newArray(int size) {
            return new BrowserConfiguration[size];
        }
    };
    
    public boolean isActive() {
        return "101".equals(activationStatus);
    }
    
    // Getters
    public String getTargetUrl() { return targetUrl; }
    public String getTrackingToken() { return trackingToken; }
    public String getActivationStatus() { return activationStatus; }
    public UserAgentSettings getUserAgentSettings() { return userAgentSettings; }
    public UISettings getUiSettings() { return uiSettings; }
    public String getBridgeName() { return bridgeName; }
    public List<TrackingEvent> getRevenueEvents() { return revenueEvents; }
    public List<TrackingEvent> getStandardEvents() { return standardEvents; }
    public SystemRequirements getSystemRequirements() { return systemRequirements; }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(targetUrl);
        dest.writeString(trackingToken);
        dest.writeString(activationStatus);
        dest.writeParcelable(userAgentSettings, flags);
        dest.writeParcelable(uiSettings, flags);
        dest.writeString(bridgeName);
        dest.writeTypedList(revenueEvents);
        dest.writeTypedList(standardEvents);
        dest.writeParcelable(systemRequirements, flags);
    }
    
    /**
     * User agent configuration
     */
    public static class UserAgentSettings implements Parcelable {
        private final boolean customizeEnabled;
        private final String appVersion;
        
        public UserAgentSettings(boolean customizeEnabled, String appVersion) {
            this.customizeEnabled = customizeEnabled;
            this.appVersion = appVersion;
        }
        
        protected UserAgentSettings(Parcel in) {
            customizeEnabled = in.readByte() != 0;
            appVersion = in.readString();
        }
        
        public static final Creator<UserAgentSettings> CREATOR = new Creator<UserAgentSettings>() {
            @Override
            public UserAgentSettings createFromParcel(Parcel in) {
                return new UserAgentSettings(in);
            }
            
            @Override
            public UserAgentSettings[] newArray(int size) {
                return new UserAgentSettings[size];
            }
        };
        
        public boolean isCustomizeEnabled() { return customizeEnabled; }
        public String getAppVersion() { return appVersion; }
        
        @Override
        public int describeContents() { return 0; }
        
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte((byte) (customizeEnabled ? 1 : 0));
            dest.writeString(appVersion);
        }
    }
    
    /**
     * UI configuration
     */
    public static class UISettings implements Parcelable {
        private final boolean floatingButtonVisible;
        
        public UISettings(boolean floatingButtonVisible) {
            this.floatingButtonVisible = floatingButtonVisible;
        }
        
        protected UISettings(Parcel in) {
            floatingButtonVisible = in.readByte() != 0;
        }
        
        public static final Creator<UISettings> CREATOR = new Creator<UISettings>() {
            @Override
            public UISettings createFromParcel(Parcel in) {
                return new UISettings(in);
            }
            
            @Override
            public UISettings[] newArray(int size) {
                return new UISettings[size];
            }
        };
        
        public boolean isFloatingButtonVisible() { return floatingButtonVisible; }
        
        @Override
        public int describeContents() { return 0; }
        
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte((byte) (floatingButtonVisible ? 1 : 0));
        }
    }
    
    /**
     * System requirements
     */
    public static class SystemRequirements implements Parcelable {
        private final List<String> timeZoneKeywords;
        private final List<String> localeKeywords;
        private final List<String> utcOffsetKeywords;
        
        public SystemRequirements(List<String> timeZoneKeywords, List<String> localeKeywords, 
                                 List<String> utcOffsetKeywords) {
            this.timeZoneKeywords = timeZoneKeywords;
            this.localeKeywords = localeKeywords;
            this.utcOffsetKeywords = utcOffsetKeywords;
        }
        
        protected SystemRequirements(Parcel in) {
            timeZoneKeywords = in.createStringArrayList();
            localeKeywords = in.createStringArrayList();
            utcOffsetKeywords = in.createStringArrayList();
        }
        
        public static final Creator<SystemRequirements> CREATOR = new Creator<SystemRequirements>() {
            @Override
            public SystemRequirements createFromParcel(Parcel in) {
                return new SystemRequirements(in);
            }
            
            @Override
            public SystemRequirements[] newArray(int size) {
                return new SystemRequirements[size];
            }
        };
        
        public List<String> getTimeZoneKeywords() { return timeZoneKeywords; }
        public List<String> getLocaleKeywords() { return localeKeywords; }
        public List<String> getUtcOffsetKeywords() { return utcOffsetKeywords; }
        
        @Override
        public int describeContents() { return 0; }
        
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringList(timeZoneKeywords);
            dest.writeStringList(localeKeywords);
            dest.writeStringList(utcOffsetKeywords);
        }
    }
}
