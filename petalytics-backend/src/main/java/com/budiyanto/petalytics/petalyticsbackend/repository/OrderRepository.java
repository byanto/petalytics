package com.budiyanto.petalytics.petalyticsbackend.repository;

import com.budiyanto.petalytics.petalyticsbackend.domain.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.domain.Order;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.ChannelSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.LocationSummaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.error.Mark;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
        SELECT new com.budiyanto.petalytics.petalyticsbackend.domain.dto.LocationSummaryDto(
                o.province, o.city, COUNT(o), SUM(o.totalAmount)
        )
        FROM Order o
        WHERE (:marketplace IS NULL OR o.marketplace = :marketplace)
          AND (cast(:startDate as timestamp) IS NULL OR o.orderDate >= :startDate)
          AND (cast(:endDate as timestamp) IS NULL OR o.orderDate <= :endDate)
        GROUP BY o.province, o.city
        ORDER BY o.province ASC, o.city ASC
    """)
    List<LocationSummaryDto> findOrderSummaryByLocation(
            @Param("marketplace") Marketplace marketplace,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
        SELECT new com.budiyanto.petalytics.petalyticsbackend.domain.dto.ChannelSummaryDto(
                o.marketplace, COUNT(o), SUM(o.totalAmount)
        )
        FROM Order o
        WHERE (:marketplace IS NULL OR o.marketplace = :marketplace)
          AND (cast(:startDate as timestamp) IS NULL OR o.orderDate >= :startDate)
          AND (cast(:endDate as timestamp) IS NULL OR o.orderDate <= :endDate)
        GROUP BY o.marketplace
        ORDER BY o.marketplace ASC
    """)
    List<ChannelSummaryDto> findOrderSummaryByChannel(
            @Param("marketplace") Marketplace marketplace,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query("SELECT o.orderNo FROM Order o WHERE o.orderNo IN :orderNos")
    List<String> findExistingOrderNosFromGivenList(List<String> orderNos);
}
