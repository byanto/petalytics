package com.budiyanto.petalytics.petalyticsbackend.analytics.domain.model;

import java.math.BigDecimal;

public record LocationSummaryDto(
        String province,
        String city,
        Long totalOrders,
        BigDecimal totalRevenue
) {}
