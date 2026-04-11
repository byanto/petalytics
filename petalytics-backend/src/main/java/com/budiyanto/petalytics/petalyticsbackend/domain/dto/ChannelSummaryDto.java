package com.budiyanto.petalytics.petalyticsbackend.domain.dto;

import com.budiyanto.petalytics.petalyticsbackend.domain.Marketplace;

import java.math.BigDecimal;

public record ChannelSummaryDto(
        Marketplace marketplace,
        Long totalOrders,
        BigDecimal totalRevenue
) {}
