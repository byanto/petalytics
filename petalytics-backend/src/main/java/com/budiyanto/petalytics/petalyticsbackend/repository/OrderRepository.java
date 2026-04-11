package com.budiyanto.petalytics.petalyticsbackend.repository;

import com.budiyanto.petalytics.petalyticsbackend.domain.Order;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.ChannelSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.LocationSummaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
        SELECT new com.budiyanto.petalytics.petalyticsbackend.domain.dto.LocationSummaryDto(
                o.shippingProvince, o.shippingCity, COUNT(o), SUM(o.totalAmount)
        )
        FROM Order o
        GROUP BY o.shippingProvince, o.shippingCity
        ORDER BY o.shippingProvince, o.shippingCity ASC
    """)
    List<LocationSummaryDto> retrieveOrderSummaryByLocation();

    @Query("""
        SELECT new com.budiyanto.petalytics.petalyticsbackend.domain.dto.ChannelSummaryDto(
                o.marketplace, COUNT(o), SUM(o.totalAmount)
        )
        FROM Order o
        GROUP BY o.marketplace
        ORDER BY o.marketplace ASC
    """)
    List<ChannelSummaryDto> retrieveOrderSummaryByChannel();
}
