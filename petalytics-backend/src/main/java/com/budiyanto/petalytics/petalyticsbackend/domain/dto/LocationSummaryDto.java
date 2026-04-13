package com.budiyanto.petalytics.petalyticsbackend.domain.dto;

import java.math.BigDecimal;

public record LocationSummaryDto(
        String province,
        String city,
        Long totalOrders,
        BigDecimal totalRevenue
) {}
