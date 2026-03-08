package com.nexustech.smartbrowser.device;

import android.content.res.Resources;
import android.os.Build;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

/**
 * Functional system validator with predicates
 */
public class SystemValidator {
    
    private final BiPredicate<List<String>, String> matcher = (keywords, value) ->
        keywords != null && !keywords.isEmpty() &&
        keywords.stream()
            .filter(k -> k != null && !k.isEmpty())
            .anyMatch(k -> value.toLowerCase().contains(k.toLowerCase()));
    
    public boolean validate(List<String> tzKeywords, List<String> localeKeywords, List<String> utcKeywords) {
        return validateTimeZone(tzKeywords) && 
               validateLocale(localeKeywords) && 
               validateUtcOffset(utcKeywords);
    }
    
    private boolean validateTimeZone(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) return true;
        String zoneId = TimeZone.getDefault().getID();
        return matcher.test(keywords, zoneId);
    }
    
    private boolean validateLocale(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) return true;
        Locale locale = getCurrentLocale();
        String tag = locale.toLanguageTag();
        String lang = locale.getLanguage();
        return matcher.test(keywords, tag) || matcher.test(keywords, lang);
    }
    
    private boolean validateUtcOffset(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) return true;
        int offsetHours = TimeZone.getDefault().getRawOffset() / (1000 * 60 * 60);
        String offsetStr = String.valueOf(offsetHours);
        return keywords.contains(offsetStr);
    }
    
    private Locale getCurrentLocale() {
        android.content.res.Configuration config = Resources.getSystem().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            android.os.LocaleList locales = config.getLocales();
            return locales.isEmpty() ? Locale.getDefault() : locales.get(0);
        }
        return config.locale;
    }
}
