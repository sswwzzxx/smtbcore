package com.nexustech.smartbrowser.data.mapper;

import com.nexustech.smartbrowser.data.dto.ConfigurationDto;
import com.nexustech.smartbrowser.data.dto.EventDto;
import com.nexustech.smartbrowser.domain.BrowserConfiguration;
import com.nexustech.smartbrowser.domain.TrackingEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Maps between DTOs and domain models
 */
public class ConfigurationMapper {
    
    private final EventMapper eventMapper;
    
    public ConfigurationMapper() {
        this.eventMapper = new EventMapper();
    }
    
    public BrowserConfiguration toDomain(ConfigurationDto dto) {
        BrowserConfiguration.UserAgentSettings userAgentSettings = 
            new BrowserConfiguration.UserAgentSettings(
                "1".equals(dto.getUserAgentType()),
                dto.getUserAgentVersion()
            );
        
        BrowserConfiguration.UISettings uiSettings = 
            new BrowserConfiguration.UISettings(
                "1".equals(dto.getFloatingButtonShow())
            );
        
        BrowserConfiguration.SystemRequirements systemRequirements = 
            new BrowserConfiguration.SystemRequirements(
                dto.getTimeZones(),
                dto.getSystemLocales(),
                dto.getUtcOffsets()
            );
        
        List<TrackingEvent> revenueEvents = dto.getRevenueEvents() != null
            ? eventMapper.toDomainList(dto.getRevenueEvents())
            : Collections.emptyList();
        
        List<TrackingEvent> standardEvents = dto.getOtherEvents() != null
            ? eventMapper.toDomainList(dto.getOtherEvents())
            : Collections.emptyList();
        
        return new BrowserConfiguration(
            dto.getUrl() != null ? dto.getUrl() : "",
            dto.getTrackingToken(),
            dto.getStatus() != null ? dto.getStatus() : "100",
            userAgentSettings,
            uiSettings,
            dto.getJsBridgeName(),
            revenueEvents,
            standardEvents,
            systemRequirements
        );
    }
    
    public ConfigurationDto toDto(BrowserConfiguration domain) {
        ConfigurationDto dto = new ConfigurationDto();
        dto.setUrl(domain.getTargetUrl());
        dto.setTrackingToken(domain.getTrackingToken());
        dto.setStatus(domain.getActivationStatus());
        dto.setUserAgentType(domain.getUserAgentSettings().isCustomizeEnabled() ? "1" : "0");
        dto.setUserAgentVersion(domain.getUserAgentSettings().getAppVersion());
        dto.setFloatingButtonShow(domain.getUiSettings().isFloatingButtonVisible() ? "1" : "0");
        dto.setJsBridgeName(domain.getBridgeName());
        dto.setRevenueEvents(eventMapper.toDtoList(domain.getRevenueEvents()));
        dto.setOtherEvents(eventMapper.toDtoList(domain.getStandardEvents()));
        dto.setTimeZones(domain.getSystemRequirements().getTimeZoneKeywords());
        dto.setSystemLocales(domain.getSystemRequirements().getLocaleKeywords());
        dto.setUtcOffsets(domain.getSystemRequirements().getUtcOffsetKeywords());
        return dto;
    }
    
    /**
     * Inner mapper for events
     */
    private static class EventMapper {
        
        public List<TrackingEvent> toDomainList(List<EventDto> dtoList) {
            List<TrackingEvent> domainList = new ArrayList<>();
            for (EventDto dto : dtoList) {
                TrackingEvent domain = toDomain(dto);
                if (domain != null) {
                    domainList.add(domain);
                }
            }
            return domainList;
        }
        
        public List<EventDto> toDtoList(List<TrackingEvent> domainList) {
            List<EventDto> dtoList = new ArrayList<>();
            for (TrackingEvent domain : domainList) {
                dtoList.add(toDto(domain));
            }
            return dtoList;
        }
        
        public TrackingEvent toDomain(EventDto dto) {
            String eventName = dto.getEventName();
            String eventToken = dto.getToken();
            
            if (eventName == null || eventToken == null) {
                return null;
            }
            
            TrackingEvent.NavigationAction navigationAction = 
                new TrackingEvent.NavigationAction(
                    "1".equals(dto.getNavigationEnabled()),
                    dto.getNavigationUrl(),
                    dto.getNavigationMessage()
                );
            
            TrackingEvent.DialogSettings dialogSettings = 
                new TrackingEvent.DialogSettings(
                    dto.getDialogTitle(),
                    dto.getDialogConfirm(),
                    dto.getDialogCancel()
                );
            
            return new TrackingEvent(eventName, eventToken, navigationAction, dialogSettings);
        }
        
        public EventDto toDto(TrackingEvent domain) {
            EventDto dto = new EventDto();
            dto.setEventName(domain.getEventName());
            dto.setToken(domain.getEventToken());
            
            if (domain.getNavigationAction() != null) {
                dto.setNavigationEnabled(domain.getNavigationAction().isEnabled() ? "1" : "0");
                dto.setNavigationUrl(domain.getNavigationAction().getTargetUrl());
                dto.setNavigationMessage(domain.getNavigationAction().getMessage());
            }
            
            if (domain.getDialogSettings() != null) {
                dto.setDialogTitle(domain.getDialogSettings().getTitle());
                dto.setDialogConfirm(domain.getDialogSettings().getConfirmText());
                dto.setDialogCancel(domain.getDialogSettings().getCancelText());
            }
            
            return dto;
        }
    }
}
