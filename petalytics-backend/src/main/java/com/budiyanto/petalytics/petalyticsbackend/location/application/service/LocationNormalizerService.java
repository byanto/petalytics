package com.budiyanto.petalytics.petalyticsbackend.location.application.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.budiyanto.petalytics.petalyticsbackend.location.application.port.in.NormalizeLocationUseCase;
import com.budiyanto.petalytics.petalyticsbackend.location.application.port.out.LocationMappingRepositoryPort;
import com.budiyanto.petalytics.petalyticsbackend.location.domain.model.LocationMapping;
import com.budiyanto.petalytics.petalyticsbackend.location.domain.model.LocationType;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class LocationNormalizerService implements NormalizeLocationUseCase {

    private final LocationMappingRepositoryPort mappingRepositoryPort;

    // High-performance in-memory cache to prevent N+1 queries during data ingestion
    private final Map<String, String> provinceMap = new ConcurrentHashMap<>();
    private final Map<String, String> cityMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadMappingCache() {
        log.info("Loading location mapping rules into memory cache...");
        List<LocationMapping> mappings = mappingRepositoryPort.findAll();
        for (LocationMapping mapping: mappings) {
            if (mapping.getLocationType().equals(LocationType.PROVINCE)) {
                provinceMap.put(mapping.getRawName().toLowerCase(), mapping.getStandardizedName());
            } else {
                cityMap.put(mapping.getRawName().toLowerCase(), mapping.getStandardizedName());
            }
        }
        log.info("Successfully cached location mapping rules.");
    }
    
    public String normalizeProvince(String rawProvince) {
        if (rawProvince == null || rawProvince.isBlank()) {
            return "Unknown Province";
        }
        String trimmed = rawProvince.trim();
        return provinceMap.getOrDefault(trimmed.toLowerCase(), toTitleCase(trimmed));
    }

    public String normalizeCity(String rawCity) {
        if (rawCity == null || rawCity.isBlank()) {
            return "Unknown City";
        }
        String trimmed = rawCity.trim();
        String lowerCase = trimmed.toLowerCase();

        if (cityMap.containsKey(lowerCase)) {
            return cityMap.get(lowerCase);
        }

        if (lowerCase.startsWith("kab.")) {
            return "Kabupaten " + toTitleCase(trimmed.substring(5).trim());
        }

        return toTitleCase(trimmed);
    }

    private String toTitleCase(String text) {        
        String[] words = text.split("\\s+");
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            titleCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
        }
        return titleCase.toString().trim();
    }
}
