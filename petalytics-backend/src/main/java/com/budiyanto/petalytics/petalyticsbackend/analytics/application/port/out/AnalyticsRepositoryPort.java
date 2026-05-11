package com.budiyanto.petalytics.petalyticsbackend.analytics.application.port.out;

import java.time.LocalDateTime;
import java.util.List;

import com.budiyanto.petalytics.petalyticsbackend.analytics.domain.model.ChannelSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.analytics.domain.model.LocationSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Marketplace;

public interface AnalyticsRepositoryPort {

    List<LocationSummaryDto> findOrderSummaryByLocation(Marketplace marketplace, LocalDateTime startDate, LocalDateTime endDate);
    List<ChannelSummaryDto> findOrderSummaryByChannel(Marketplace marketplace, LocalDateTime startDate, LocalDateTime endDate);
    
}
