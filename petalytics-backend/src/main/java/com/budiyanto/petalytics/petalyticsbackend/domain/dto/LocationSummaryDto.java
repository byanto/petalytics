package com.budiyanto.petalytics.petalyticsbackend.domain.dto;

import java.math.BigDecimal;

public record LocationSummaryDto(
        String shippingProvince,
        String shippingCity,
        Long totalOrders,
        BigDecimal totalRevenue
) {}
