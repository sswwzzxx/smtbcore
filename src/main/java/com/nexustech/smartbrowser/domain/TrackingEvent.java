package com.nexustech.smartbrowser.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Tracking event configuration
 */
public class TrackingEvent implements Parcelable {
    private final String eventName;
    private final String eventToken;
    private final NavigationAction navigationAction;
    private final DialogSettings dialogSettings;
    
    public TrackingEvent(String eventName, String eventToken, NavigationAction navigationAction,
                        DialogSettings dialogSettings) {
        this.eventName = eventName;
        this.eventToken = eventToken;
        this.navigationAction = navigationAction;
        this.dialogSettings = dialogSettings;
    }
    
    protected TrackingEvent(Parcel in) {
        eventName = in.readString();
        eventToken = in.readString();
        navigationAction = in.readParcelable(NavigationAction.class.getClassLoader());
        dialogSettings = in.readParcelable(DialogSettings.class.getClassLoader());
    }
    
    public static final Creator<TrackingEvent> CREATOR = new Creator<TrackingEvent>() {
        @Override
        public TrackingEvent createFromParcel(Parcel in) {
            return new TrackingEvent(in);
        }
        
        @Override
        public TrackingEvent[] newArray(int size) {
            return new TrackingEvent[size];
        }
    };
    
    public boolean shouldNavigate() {
        return navigationAction != null && navigationAction.isEnabled();
    }
    
    public String getEventName() { return eventName; }
    public String getEventToken() { return eventToken; }
    public NavigationAction getNavigationAction() { return navigationAction; }
    public DialogSettings getDialogSettings() { return dialogSettings; }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventName);
        dest.writeString(eventToken);
        dest.writeParcelable(navigationAction, flags);
        dest.writeParcelable(dialogSettings, flags);
    }
    
    /**
     * Navigation action configuration
     */
    public static class NavigationAction implements Parcelable {
        private final boolean enabled;
        private final String targetUrl;
        private final String message;
        
        public NavigationAction(boolean enabled, String targetUrl, String message) {
            this.enabled = enabled;
            this.targetUrl = targetUrl;
            this.message = message;
        }
        
        protected NavigationAction(Parcel in) {
            enabled = in.readByte() != 0;
            targetUrl = in.readString();
            message = in.readString();
        }
        
        public static final Creator<NavigationAction> CREATOR = new Creator<NavigationAction>() {
            @Override
            public NavigationAction createFromParcel(Parcel in) {
                return new NavigationAction(in);
            }
            
            @Override
            public NavigationAction[] newArray(int size) {
                return new NavigationAction[size];
            }
        };
        
        public boolean isEnabled() { return enabled; }
        public String getTargetUrl() { return targetUrl; }
        public String getMessage() { return message; }
        
        @Override
        public int describeContents() { return 0; }
        
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte((byte) (enabled ? 1 : 0));
            dest.writeString(targetUrl);
            dest.writeString(message);
        }
    }
    
    /**
     * Dialog display settings
     */
    public static class DialogSettings implements Parcelable {
        private final String title;
        private final String confirmText;
        private final String cancelText;
        
        public DialogSettings(String title, String confirmText, String cancelText) {
            this.title = title;
            this.confirmText = confirmText;
            this.cancelText = cancelText;
        }
        
        protected DialogSettings(Parcel in) {
            title = in.readString();
            confirmText = in.readString();
            cancelText = in.readString();
        }
        
        public static final Creator<DialogSettings> CREATOR = new Creator<DialogSettings>() {
            @Override
            public DialogSettings createFromParcel(Parcel in) {
                return new DialogSettings(in);
            }
            
            @Override
            public DialogSettings[] newArray(int size) {
                return new DialogSettings[size];
            }
        };
        
        public String getTitle() { return title; }
        public String getConfirmText() { return confirmText; }
        public String getCancelText() { return cancelText; }
        
        @Override
        public int describeContents() { return 0; }
        
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(confirmText);
            dest.writeString(cancelText);
        }
    }
}
