package com.nexustech.smartbrowser.device;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Predicate;

/**
 * Functional region detector with stream API
 */
public class RegionDetector {
    
    private static final Set<String> INDONESIA_ZONES = new HashSet<>(Arrays.asList(
        "Asia/Jakarta", "Asia/Pontianak", "Asia/Makassar", "Asia/Jayapura"
    ));
    
    private static final Set<String> VIETNAM_ZONES = new HashSet<>(Arrays.asList(
        "Asia/Ho_Chi_Minh", "Asia/Saigon"
    ));
    
    private static final Set<String> CAMBODIA_ZONES = new HashSet<>(Arrays.asList(
        "Asia/Phnom_Penh"
    ));
    
    private static final Set<String> BRAZIL_ZONES = new HashSet<>(Arrays.asList(
        "America/Noronha", "America/Belem", "America/Fortaleza", "America/Recife",
        "America/Araguaina", "America/Maceio", "America/Bahia", "America/Sao_Paulo",
        "America/Campo_Grande", "America/Cuiaba", "America/Santarem", "America/Porto_Velho",
        "America/Boa_Vista", "America/Manaus", "America/Eirunepe", "America/Rio_Branco"
    ));
    
    private static final List<String> MEXICO_PREFIXES = Arrays.asList(
        "Mexico/", "America/Mexico_City", "America/Cancun", "America/Merida",
        "America/Monterrey", "America/Mazatlan", "America/Chihuahua",
        "America/Hermosillo", "America/Tijuana", "America/Bahia_Banderas"
    );
    
    public boolean isTargetRegion() {
        return isIndonesia() || isVietnam() || isCambodia();
    }
    
    public boolean isExtendedRegion() {
        return isBrazil() || isMexico() || isVietnam() || isCambodia();
    }
    
    private boolean isIndonesia() {
        return matchesZone(INDONESIA_ZONES::contains);
    }
    
    private boolean isVietnam() {
        return matchesZone(VIETNAM_ZONES::contains);
    }
    
    private boolean isCambodia() {
        return matchesZone(CAMBODIA_ZONES::contains);
    }
    
    private boolean isBrazil() {
        return matchesZone(BRAZIL_ZONES::contains);
    }
    
    private boolean isMexico() {
        String zoneId = TimeZone.getDefault().getID();
        return MEXICO_PREFIXES.stream()
            .anyMatch(zoneId::startsWith);
    }
    
    private boolean matchesZone(Predicate<String> matcher) {
        String zoneId = TimeZone.getDefault().getID();
        return zoneId != null && matcher.test(zoneId);
    }
}
