package com.budiyanto.petalytics.petalyticsbackend.analytics.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

import com.budiyanto.petalytics.petalyticsbackend.analytics.domain.model.ChannelSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.analytics.domain.model.LocationSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Marketplace;

public interface RetrieveAnalyticsUseCase {

    List<LocationSummaryDto> retrieveOrderSummaryByLocation(Marketplace marketplace, LocalDateTime startDate, LocalDateTime endDate);
    List<ChannelSummaryDto> retrieveOrderSummaryByChannel(Marketplace marketplace, LocalDateTime startDate, LocalDateTime endDate);

}
