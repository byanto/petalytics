package com.budiyanto.petalytics.petalyticsbackend.analytics.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.budiyanto.petalytics.petalyticsbackend.analytics.application.port.in.RetrieveAnalyticsUseCase;
import com.budiyanto.petalytics.petalyticsbackend.analytics.application.port.out.AnalyticsRepositoryPort;
import com.budiyanto.petalytics.petalyticsbackend.analytics.domain.model.ChannelSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.analytics.domain.model.LocationSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Marketplace;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalyticsService implements RetrieveAnalyticsUseCase {

    private final AnalyticsRepositoryPort analyticsRepositoryPort;

    public List<LocationSummaryDto> retrieveOrderSummaryByLocation(Marketplace marketplace, LocalDateTime startDate, LocalDateTime endDate) {
        return analyticsRepositoryPort.findOrderSummaryByLocation(marketplace, startDate, endDate);
    }

    public List<ChannelSummaryDto> retrieveOrderSummaryByChannel(Marketplace marketplace, LocalDateTime startDate, LocalDateTime endDate) {
        return analyticsRepositoryPort.findOrderSummaryByChannel(marketplace, startDate, endDate);
    }
}
