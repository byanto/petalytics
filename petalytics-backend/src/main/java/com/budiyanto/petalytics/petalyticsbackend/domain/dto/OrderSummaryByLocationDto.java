package com.budiyanto.petalytics.petalyticsbackend.domain.dto;

import com.budiyanto.petalytics.petalyticsbackend.domain.Marketplace;

import java.math.BigDecimal;

public record OrderSummaryByLocationDto(
        String shippingProvince,
        String shippingCity,
        Long totalOrders,
        BigDecimal totalRevenue
) {}
