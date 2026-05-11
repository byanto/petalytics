package com.budiyanto.petalytics.petalyticsbackend.analytics.domain.model;

import java.math.BigDecimal;

import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Marketplace;

public record ChannelSummaryDto(
        Marketplace marketplace,
        Long totalOrders,
        BigDecimal totalRevenue
) {}
