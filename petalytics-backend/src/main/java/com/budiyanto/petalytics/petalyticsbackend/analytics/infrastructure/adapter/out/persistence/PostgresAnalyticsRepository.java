package com.budiyanto.petalytics.petalyticsbackend.analytics.infrastructure.adapter.out.persistence;

import com.budiyanto.petalytics.petalyticsbackend.analytics.application.port.out.AnalyticsRepositoryPort;
import com.budiyanto.petalytics.petalyticsbackend.analytics.domain.model.ChannelSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.analytics.domain.model.LocationSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.ordering.infrastructure.adapter.out.persistence.SpringDataOrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostgresAnalyticsRepository implements AnalyticsRepositoryPort {

    private final SpringDataOrderRepository orderRepository;

    @Override
    public List<LocationSummaryDto> findOrderSummaryByLocation(Marketplace marketplace, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findOrderSummaryByLocation(marketplace, startDate, endDate);
    }

    @Override
    public List<ChannelSummaryDto> findOrderSummaryByChannel(Marketplace marketplace, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findOrderSummaryByChannel(marketplace, startDate, endDate);
    }
}
