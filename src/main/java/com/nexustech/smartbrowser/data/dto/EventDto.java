package com.nexustech.smartbrowser.data.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Data transfer object for event configuration
 */
public class EventDto {
    @SerializedName("event")
    private String eventName;
    
    @SerializedName("eventToken")
    private String token;
    
    @SerializedName("jumpUrl")
    private String navigationUrl;
    
    @SerializedName("isJump")
    private String navigationEnabled;
    
    @SerializedName("jumpContent")
    private String navigationMessage;
    
    @SerializedName("dialogTitle")
    private String dialogTitle;
    
    @SerializedName("dialogOk")
    private String dialogConfirm;
    
    @SerializedName("dialogCancel")
    private String dialogCancel;
    
    // Getters and Setters
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getNavigationUrl() { return navigationUrl; }
    public void setNavigationUrl(String navigationUrl) { this.navigationUrl = navigationUrl; }
    
    public String getNavigationEnabled() { return navigationEnabled; }
    public void setNavigationEnabled(String navigationEnabled) { this.navigationEnabled = navigationEnabled; }
    
    public String getNavigationMessage() { return navigationMessage; }
    public void setNavigationMessage(String navigationMessage) { this.navigationMessage = navigationMessage; }
    
    public String getDialogTitle() { return dialogTitle; }
    public void setDialogTitle(String dialogTitle) { this.dialogTitle = dialogTitle; }
    
    public String getDialogConfirm() { return dialogConfirm; }
    public void setDialogConfirm(String dialogConfirm) { this.dialogConfirm = dialogConfirm; }
    
    public String getDialogCancel() { return dialogCancel; }
    public void setDialogCancel(String dialogCancel) { this.dialogCancel = dialogCancel; }
}
