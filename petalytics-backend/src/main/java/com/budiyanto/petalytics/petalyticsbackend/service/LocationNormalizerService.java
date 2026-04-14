package com.budiyanto.petalytics.petalyticsbackend.service;

import com.budiyanto.petalytics.petalyticsbackend.domain.LocationMapping;
import com.budiyanto.petalytics.petalyticsbackend.domain.LocationType;
import com.budiyanto.petalytics.petalyticsbackend.repository.LocationMappingRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationNormalizerService {

    private final LocationMappingRepository mappingRepository;

    // High-performance in-memory cache to prevent N+1 queries during data ingestion
    private final Map<String, String> provinceMap = new ConcurrentHashMap<>();
    private final Map<String, String> cityMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadMappingCache() {
        log.info("Loading location mapping rules into memory cache...");
        List<LocationMapping> mappings = mappingRepository.findAll();
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
        if (text == null || text.isBlank()) {
            return text;
        }
        String[] words = text.split("\\s+");
        StringBuilder titleCase = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                         .append(word.substring(1).toLowerCase())
                         .append(" ");
            }
        }
        return titleCase.toString().trim();
    }
}
