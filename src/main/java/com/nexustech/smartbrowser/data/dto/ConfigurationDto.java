package com.nexustech.smartbrowser.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Data transfer object for configuration API response
 */
public class ConfigurationDto {
    @SerializedName("data")
    private String url;
    
    @SerializedName("adtoken")
    private String trackingToken;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("agentType")
    private String userAgentType;
    
    @SerializedName("agentAppVer")
    private String userAgentVersion;
    
    @SerializedName("floatingShow")
    private String floatingButtonShow;
    
    @SerializedName("bridgeName")
    private String jsBridgeName;
    
    @SerializedName("chargeEvent")
    private List<EventDto> revenueEvents;
    
    @SerializedName("otherEvent")
    private List<EventDto> otherEvents;
    
    @SerializedName("tz")
    private List<String> timeZones;
    
    @SerializedName("sl")
    private List<String> systemLocales;
    
    @SerializedName("utc")
    private List<String> utcOffsets;
    
    // Getters and Setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getTrackingToken() { return trackingToken; }
    public void setTrackingToken(String trackingToken) { this.trackingToken = trackingToken; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getUserAgentType() { return userAgentType; }
    public void setUserAgentType(String userAgentType) { this.userAgentType = userAgentType; }
    
    public String getUserAgentVersion() { return userAgentVersion; }
    public void setUserAgentVersion(String userAgentVersion) { this.userAgentVersion = userAgentVersion; }
    
    public String getFloatingButtonShow() { return floatingButtonShow; }
    public void setFloatingButtonShow(String floatingButtonShow) { this.floatingButtonShow = floatingButtonShow; }
    
    public String getJsBridgeName() { return jsBridgeName; }
    public void setJsBridgeName(String jsBridgeName) { this.jsBridgeName = jsBridgeName; }
    
    public List<EventDto> getRevenueEvents() { return revenueEvents; }
    public void setRevenueEvents(List<EventDto> revenueEvents) { this.revenueEvents = revenueEvents; }
    
    public List<EventDto> getOtherEvents() { return otherEvents; }
    public void setOtherEvents(List<EventDto> otherEvents) { this.otherEvents = otherEvents; }
    
    public List<String> getTimeZones() { return timeZones; }
    public void setTimeZones(List<String> timeZones) { this.timeZones = timeZones; }
    
    public List<String> getSystemLocales() { return systemLocales; }
    public void setSystemLocales(List<String> systemLocales) { this.systemLocales = systemLocales; }
    
    public List<String> getUtcOffsets() { return utcOffsets; }
    public void setUtcOffsets(List<String> utcOffsets) { this.utcOffsets = utcOffsets; }
}
