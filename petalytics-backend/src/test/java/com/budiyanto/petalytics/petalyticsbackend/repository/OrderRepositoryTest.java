package com.budiyanto.petalytics.petalyticsbackend.repository;

import com.budiyanto.petalytics.petalyticsbackend.domain.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.domain.Order;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.ChannelSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.LocationSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.budiyanto.petalytics.petalyticsbackend.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@DisplayName("Order Repository Integration Tests")
class OrderRepositoryTest {

    private final TestEntityManager entityManager;
    private final OrderRepository orderRepository;

    @Autowired
    OrderRepositoryTest(TestEntityManager entityManager, OrderRepository orderRepository){
        this.entityManager = entityManager;
        this.orderRepository = orderRepository;
    }

    @Nested
    @DisplayName("Persistence Operations")
    class PersistenceOperations {

        @Test
        @DisplayName("Given a valid order with items, when saved, then it should persist correctly with cascaded items and versioning")
        void given_validOrderWithItems_when_saved_then_persistCorrectlyWithCascadedItemsAndVersioning() {
            // Given
            Order order = Order.create("2601020X4SFPSA", Marketplace.SHOPEE, LocalDateTime.now(), "user1", "Sumatera Utara", "Kota Medan", LocalDateTime.now().plusDays(5));
            order.addOrderItem("SKU 1", "Product A", new BigDecimal("100.00"), 20);
            order.addOrderItem("SKU 2", "Product B", new BigDecimal("150.00"), 15);

            // When
            // We save the order. Spring Data should automatically execute the INSERTs and update the @Version field
            Order savedOrder = orderRepository.saveAndFlush(order);

            // We clear the persistence context to force the next findById to hit the actual database
            entityManager.clear();

            // Then
            Order retrievedOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
            then(retrievedOrder).isNotNull();
            then(retrievedOrder.getId()).isEqualTo(savedOrder.getId());

            // Verify Optimistic Locking initialized the version at 0
            then(retrievedOrder.getVersion()).isEqualTo(0);

            // Verify the totals were persisted
            then(retrievedOrder.getTotalQuantity()).isEqualTo(35);
            then(retrievedOrder.getTotalAmount()).isEqualByComparingTo("4250.00");

            // Verify CascadeType.ALL successfully saved the OrderItems into the database
            then(retrievedOrder.getOrderItems()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Analytics Queries")
    class AnalyticsQueries {

        @BeforeEach
        void setUpAnalyticsData() {
            // Given
            Order order1 = Order.create("2601020X4SFPSA", Marketplace.SHOPEE, LocalDateTime.now(), "user1", "Sumatera Utara", "Kota Medan", LocalDateTime.now().plusDays(5));
            order1.addOrderItem("SKU 1", "Product A", new BigDecimal("50"), 15);
            order1.addOrderItem("SKU 2", "Product B", new BigDecimal("250"), 5);
            order1.addOrderItem("SKU 3", "Product C", new BigDecimal("150"), 10);

            Order order2 = Order.create("2601030X4SUWKW", Marketplace.SHOPEE, LocalDateTime.now(), "user2", "Sumatera Utara", "Kab. Simalungun", LocalDateTime.now().plusDays(3));
            order2.addOrderItem("SKU 1", "Product A", new BigDecimal("50"), 10);
            order2.addOrderItem("SKU 4", "Product D", new BigDecimal("300"), 20);
            order2.addOrderItem("SKU 5", "Product E", new BigDecimal("200"), 15);

            Order order3 = Order.create("2601040X4SFYTW", Marketplace.TIKTOK, LocalDateTime.now(), "user1", "Sumatera Utara", "Kota Medan", LocalDateTime.now().plusDays(2));
            order3.addOrderItem("SKU 1", "Product A", new BigDecimal("50"), 35);
            order3.addOrderItem("SKU 2", "Product B", new BigDecimal("250"), 15);
            order3.addOrderItem("SKU 5", "Product E", new BigDecimal("200"), 20);

            Order order4 = Order.create("2601050X4SUIWL", Marketplace.TIKTOK, LocalDateTime.now(), "user3", "Sumatera Selatan", "Kota Palembang", LocalDateTime.now().plusDays(4));
            order4.addOrderItem("SKU 2", "Product B", new BigDecimal("250"), 25);
            order4.addOrderItem("SKU 3", "Product C", new BigDecimal("150"), 10);
            order4.addOrderItem("SKU 5", "Product E", new BigDecimal("200"), 5);

            orderRepository.saveAllAndFlush(List.of(order1, order2, order3, order4));
            entityManager.clear();
        }

        @Test
        @DisplayName("Given existing orders, when retrieveOrderSummaryByLocation, then returns aggregated metrics")
        void given_existingOrders_when_retrieveOrderSummaryByLocation_then_returnsAggregatedMetrics() {
            // Given (handled by @BeforeEach)
            // When
            List<LocationSummaryDto> result = orderRepository.findOrderSummaryByLocation();

            // Then
            then(result).hasSize(3);
            then(result.getFirst().province()).isEqualTo("Sumatera Selatan");
            LocationSummaryDto summary = result.stream().filter(
                    r -> r.province().equals("Sumatera Utara") && r.city().equals("Kota Medan")
            ).findFirst().orElseThrow();
            then(summary.province()).isEqualTo("Sumatera Utara");
            then(summary.totalOrders()).isEqualTo(2);
            then(summary.totalRevenue()).isEqualByComparingTo("13000");
        }

        @Test
        @DisplayName("Given existing orders, when retrieveOrderSummaryByChannel, then returns aggregated metrics")
        void given_existingOrders_when_retrieveOrderSummaryByChannel_then_returnsAggregatedMetrics() {
            // Given (handled by @BeforeEach)
            // When
            List<ChannelSummaryDto> result = orderRepository.findOrderSummaryByChannel();

            // Then
            then(result).hasSize(2);
            then(result.getFirst().marketplace()).isEqualTo(Marketplace.SHOPEE);
            ChannelSummaryDto summary = result.stream().filter(
                    r -> r.marketplace().equals(Marketplace.TIKTOK)
            ).findFirst().orElseThrow();
            then(summary.marketplace()).isEqualTo(Marketplace.TIKTOK);
            then(summary.totalOrders()).isEqualTo(2);
            then(summary.totalRevenue()).isEqualByComparingTo("18250");
        }
    }
}
