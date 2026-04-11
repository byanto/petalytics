package com.budiyanto.petalytics.petalyticsbackend.repository;

import com.budiyanto.petalytics.petalyticsbackend.domain.Order;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.OrderSummaryByLocationDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
        SELECT new com.budiyanto.petalytics.petalyticsbackend.domain.dto.OrderSummaryByLocationDto(
                o.shippingProvince, o.shippingCity, COUNT(o), SUM(o.totalAmount)
        )
        FROM Order o
        GROUP BY o.shippingProvince, o.shippingCity
        ORDER BY o.shippingProvince, o.shippingCity ASC
    """)
    List<OrderSummaryByLocationDto> retrieveOrderSummaryByLocation();

}
