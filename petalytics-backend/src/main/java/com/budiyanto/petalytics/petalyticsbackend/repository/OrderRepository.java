package com.budiyanto.petalytics.petalyticsbackend.repository;

import com.budiyanto.petalytics.petalyticsbackend.domain.Order;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.ChannelSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.LocationSummaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
        SELECT new com.budiyanto.petalytics.petalyticsbackend.domain.dto.LocationSummaryDto(
                o.province, o.city, COUNT(o), SUM(o.totalAmount)
        )
        FROM Order o
        GROUP BY o.province, o.city
        ORDER BY o.province, o.city ASC
    """)
    List<LocationSummaryDto> findOrderSummaryByLocation();

    @Query("""
        SELECT new com.budiyanto.petalytics.petalyticsbackend.domain.dto.ChannelSummaryDto(
                o.marketplace, COUNT(o), SUM(o.totalAmount)
        )
        FROM Order o
        GROUP BY o.marketplace
        ORDER BY o.marketplace ASC
    """)
    List<ChannelSummaryDto> findOrderSummaryByChannel();


    @Query("SELECT o.orderNo FROM Order o WHERE o.orderNo IN :orderNos")
    List<String> findExistingOrderNosFromGivenList(List<String> orderNos);
}
