package com.budiyanto.petalytics.petalyticsbackend.location.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.budiyanto.petalytics.petalyticsbackend.location.application.port.in.NormalizeLocationUseCase;
import com.budiyanto.petalytics.petalyticsbackend.location.application.port.out.LocationMappingRepositoryPort;
import com.budiyanto.petalytics.petalyticsbackend.location.application.service.LocationNormalizerService;

@Configuration
public class LocationConfig {
        
    @Bean
    NormalizeLocationUseCase normalizeLocationUseCase(LocationMappingRepositoryPort locationMappingRepositoryPort) {
        return new LocationNormalizerService(locationMappingRepositoryPort);
    }

}
