package com.budiyanto.petalytics.petalyticsbackend.analytics.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.budiyanto.petalytics.petalyticsbackend.analytics.application.port.in.RetrieveAnalyticsUseCase;
import com.budiyanto.petalytics.petalyticsbackend.analytics.application.port.out.AnalyticsRepositoryPort;
import com.budiyanto.petalytics.petalyticsbackend.analytics.application.service.AnalyticsService;

@Configuration
public class AnalyticsConfig {

    @Bean
    public RetrieveAnalyticsUseCase retrieveAnalyticsUseCase(AnalyticsRepositoryPort analyticsRepositoryPort) {
        return new AnalyticsService(analyticsRepositoryPort);
    }
}
