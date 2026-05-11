package com.budiyanto.petalytics.petalyticsbackend.analytics.application.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.budiyanto.petalytics.petalyticsbackend.analytics.application.port.out.AnalyticsRepositoryPort;
import com.budiyanto.petalytics.petalyticsbackend.analytics.domain.model.ChannelSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.analytics.domain.model.LocationSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Marketplace;

@DisplayName("Analytics Service Unit Tests")
@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private AnalyticsRepositoryPort analyticsRepositoryPort;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    @DisplayName("Given valid parameters when retrieveOrderSummaryByLocation, then return location summary")
    void given_validParemeters_when_retrieveOrderSummaryByLocation_then_returnLocationSummaryDto() {
        // Given
        Marketplace marketplace = Marketplace.SHOPEE;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();

        given(analyticsRepositoryPort.findOrderSummaryByLocation(marketplace, startDate, endDate)).willReturn(List.of(
            new LocationSummaryDto("Sumatera Utara", "Kota Medan", 200l, new BigDecimal(2000)),
            new LocationSummaryDto("Jawa Timur", "Kota Surabaya", 100l, new BigDecimal(1000))
        ));

        // When
        List<LocationSummaryDto> locationSummary = analyticsService.retrieveOrderSummaryByLocation(marketplace, startDate, endDate);

        // Then
        then(locationSummary).isNotEmpty();
        then(locationSummary).hasSize(2);
        then(locationSummary.get(0).province()).isEqualTo("Sumatera Utara");
        then(locationSummary.get(1).province()).isEqualTo("Jawa Timur");
    }
    
    @Test
    @DisplayName("Given valid parameters when retrieveOrderSummaryByChannel, then return channel summary")
    void given_validParemeters_when_retrieveOrderSummaryByChannel_then_returnChannelSummaryDto() {
        // Given
        Marketplace marketplace = Marketplace.SHOPEE;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now();

        given(analyticsRepositoryPort.findOrderSummaryByChannel(marketplace, startDate, endDate)).willReturn(List.of(
            new ChannelSummaryDto(Marketplace.SHOPEE, 200l, new BigDecimal(2000))
        ));

        // When
        List<ChannelSummaryDto> locationSummary = analyticsService.retrieveOrderSummaryByChannel(marketplace, startDate, endDate);

        // Then
        then(locationSummary).isNotEmpty();
        then(locationSummary).hasSize(1);
        then(locationSummary.get(0).marketplace()).isEqualTo(Marketplace.SHOPEE);
        then(locationSummary.get(0).totalOrders()).isEqualTo(200l);
    }
}
