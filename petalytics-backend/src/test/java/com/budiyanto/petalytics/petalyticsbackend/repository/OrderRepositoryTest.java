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
            LocalDateTime day1 = LocalDateTime.of(2026, 4, 10, 10, 0);
            LocalDateTime day2 = LocalDateTime.of(2026, 4, 11, 12, 0);

            Order order1 = Order.create("ORDER-DAY-1", Marketplace.SHOPEE, day1, "user1", "Sumatera Utara", "Kota Medan", null);
            order1.addOrderItem("SKU 1", "Product A", new BigDecimal("50"), 15);
            order1.addOrderItem("SKU 2", "Product B", new BigDecimal("250"), 5);
            order1.addOrderItem("SKU 3", "Product C", new BigDecimal("150"), 10); // Total Revenue: 3500, Total Quantity: 30

            Order order2 = Order.create("ORDER-DAY-2-A", Marketplace.SHOPEE, day2, "user2", "Sumatera Utara", "Kab. Simalungun", null);
            order2.addOrderItem("SKU 1", "Product A", new BigDecimal("50"), 10);
            order2.addOrderItem("SKU 4", "Product D", new BigDecimal("300"), 20);
            order2.addOrderItem("SKU 5", "Product E", new BigDecimal("200"), 15); // Total Revenue: 9500, Total Quantity: 45

            Order order3 = Order.create("ORDER-DAY-2-B", Marketplace.TIKTOK, day2, "user1", "Sumatera Utara", "Kota Medan", null);
            order3.addOrderItem("SKU 1", "Product A", new BigDecimal("50"), 35);
            order3.addOrderItem("SKU 2", "Product B", new BigDecimal("250"), 15);
            order3.addOrderItem("SKU 5", "Product E", new BigDecimal("200"), 20); // Total Revenue: 9500, Total Quantity: 70

            Order order4 = Order.create("ORDER-DAY-2-C", Marketplace.TIKTOK, day2, "user3", "Sumatera Selatan", "Kota Palembang", null);
            order4.addOrderItem("SKU 2", "Product B", new BigDecimal("250"), 25);
            order4.addOrderItem("SKU 3", "Product C", new BigDecimal("150"), 10);
            order4.addOrderItem("SKU 5", "Product E", new BigDecimal("200"), 5); // Total Revenue: 8750, Total Quantity: 40

            orderRepository.saveAllAndFlush(List.of(order1, order2, order3, order4));
            entityManager.clear();
        }

        @Test
        @DisplayName("Given existing orders, when retrieveOrderSummaryByLocation, then returns aggregated metrics")
        void given_existingOrders_when_retrieveOrderSummaryByLocation_then_returnsAggregatedMetrics() {
            // Given (handled by @BeforeEach)
            // When
            // Find all order summaries based on location
            List<LocationSummaryDto> result = orderRepository.findOrderSummaryByLocation(null, null, null);

            // Then
            then(result).hasSize(3);
            then(result.getFirst().province()).isEqualTo("Sumatera Selatan");
            var summary = result.stream().filter(
                    r -> r.province().equals("Sumatera Utara") && r.city().equals("Kota Medan")
            ).findFirst().orElseThrow();
            then(summary.province()).isEqualTo("Sumatera Utara");
            then(summary.totalOrders()).isEqualTo(2);
            then(summary.totalRevenue()).isEqualByComparingTo("13000.00"); // order1 (3500) + order3 (9500)
        }

        @Test
        @DisplayName("Given existing orders, when retrieveOrderSummaryByChannel, then returns aggregated metrics")
        void given_existingOrders_when_retrieveOrderSummaryByChannel_then_returnsAggregatedMetrics() {
            // Given (handled by @BeforeEach)
            // When
            List<ChannelSummaryDto> result = orderRepository.findOrderSummaryByChannel(null, null, null);

            // Then
            then(result).hasSize(2);
            then(result.getFirst().marketplace()).isEqualTo(Marketplace.SHOPEE);
            var summary = result.stream().filter(
                    r -> r.marketplace().equals(Marketplace.TIKTOK)
            ).findFirst().orElseThrow();
            then(summary.marketplace()).isEqualTo(Marketplace.TIKTOK);
            then(summary.totalOrders()).isEqualTo(2);
            then(summary.totalRevenue()).isEqualByComparingTo("18250.00"); // order3 (9500) + order4 (8750)
        }

        @Test
        @DisplayName("Given existing orders, when findOrderSummaryByLocation with specific marketplace, then returns filtered metrics")
        void given_existingOrders_when_filteredByMarketplaceForLocation_then_returnsCorrectAggregates() {
            // Given: @BeforeEach has orders for Shopee and TikTok.

            // When: Query data ONLY from Shopee
            List<LocationSummaryDto> result = orderRepository.findOrderSummaryByLocation(Marketplace.SHOPEE, null, null);

            // Then: It should only include Shopee orders.
            then(result).hasSize(2);
            var shopeeSummary = result.getFirst();
            then(shopeeSummary.province()).isEqualTo("Sumatera Utara");
            then(shopeeSummary.city()).isEqualTo("Kab. Simalungun");
            then(shopeeSummary.totalRevenue()).isEqualByComparingTo("9500.00"); // order2 (9500)
            then(shopeeSummary.totalOrders()).isEqualTo(1);

            // When: Query data ONLY from TikTok
            result = orderRepository.findOrderSummaryByLocation(Marketplace.TIKTOK, null, null);
            then(result).hasSize(2);
            var tiktokSummary = result.getFirst();
            then(tiktokSummary.province()).isEqualTo("Sumatera Selatan");
            then(tiktokSummary.city()).isEqualTo("Kota Palembang");
            then(tiktokSummary.totalRevenue()).isEqualByComparingTo("8750.00"); // order4 (8750)
            then(tiktokSummary.totalOrders()).isEqualTo(1);
        }

        @Test
        @DisplayName("Given existing orders, when findOrderSummaryByChannel with specific marketplace, then returns filtered metrics")
        void given_existingOrders_when_filteredByMarketplaceForChannel_then_returnsCorrectAggregates() {
            // Given: @BeforeEach has orders for Shopee and TikTok.

            // When: Query data ONLY from Shopee
            List<ChannelSummaryDto> result = orderRepository.findOrderSummaryByChannel(Marketplace.SHOPEE, null, null);

            // Then: It should only include Shopee orders.
            then(result).hasSize(1);
            var shopeeSummary = result.getFirst();
            then(shopeeSummary.marketplace()).isEqualTo(Marketplace.SHOPEE);
            then(shopeeSummary.totalRevenue()).isEqualByComparingTo("13000.00"); // order1 (3500) + order2 (9500) = 13000
            then(shopeeSummary.totalOrders()).isEqualTo(2);

            // When: Query data ONLY from TikTok
            result = orderRepository.findOrderSummaryByChannel(Marketplace.TIKTOK, null, null);
            then(result).hasSize(1);
            var tiktokSummary = result.getFirst();
            then(tiktokSummary.marketplace()).isEqualTo(Marketplace.TIKTOK);
            then(tiktokSummary.totalRevenue()).isEqualByComparingTo("18250.00"); // order3 (9500) + order4 (8750) = 18250
            then(tiktokSummary.totalOrders()).isEqualTo(2);
        }

        @Test
        @DisplayName("Given existing orders, when findOrderSummaryByLocation with date range, then returns filtered metrics")
        void given_ordersOnDifferentDays_when_filteredByDateForLocation_then_returnsCorrectAggregates() {
            // Given: @BeforeEach has orders on Day 1 (April 10) and Day 2 (April 11).

            // When: Query ONLY from Day 2
            LocalDateTime startOfDay2 = LocalDateTime.of(2026, 4, 11, 0, 0);
            LocalDateTime endOfDay2 = LocalDateTime.of(2026, 4, 11, 23, 59);

            List<LocationSummaryDto> result = orderRepository.findOrderSummaryByLocation(null, startOfDay2, endOfDay2);

            // Then: It should NOT include the order from Day 1.
            // The Day 2 data has 3 unique locations.
            then(result).hasSize(3);

            // Verify the total revenue for Medan on Day 2 is only from order3 (9500), not order1 (3500)
            var medanDay2Summary = result.stream()
                    .filter(r -> r.city().equals("Kota Medan"))
                    .findFirst().orElseThrow();

            then(medanDay2Summary.totalRevenue()).isEqualByComparingTo("9500.00");
        }

        @Test
        @DisplayName("Given existing orders, when findOrderSummaryByChannel with date range, then returns filtered metrics")
        void given_ordersOnDifferentDays_when_filteredByDateForChannel_then_returnsCorrectAggregates() {
            // Given: @BeforeEach has orders on Day 1 (April 10) and Day 2 (April 11).

            // When: Query ONLY from Day 2
            LocalDateTime startOfDay2 = LocalDateTime.of(2026, 4, 11, 0, 0);
            LocalDateTime endOfDay2 = LocalDateTime.of(2026, 4, 11, 23, 59);

            List<ChannelSummaryDto> result = orderRepository.findOrderSummaryByChannel(null, startOfDay2, endOfDay2);

            // Then: It should NOT include the order from Day 1.
            // The Day 2 data has 2 unique channels (Shopee and TikTok).
            then(result).hasSize(2);

            // Verify the total revenue for Shopee on Day 2 is only from order2 (9500)
            var shopeeDay2Summary = result.stream().filter(r -> r.marketplace().equals(Marketplace.SHOPEE)).findFirst().orElseThrow();

            then(shopeeDay2Summary.totalRevenue()).isEqualByComparingTo("9500.00"); // Only order2
        }

    }
}
