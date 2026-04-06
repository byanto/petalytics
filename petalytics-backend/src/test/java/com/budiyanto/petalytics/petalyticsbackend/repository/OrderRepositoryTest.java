package com.budiyanto.petalytics.petalyticsbackend.repository;

import com.budiyanto.petalytics.petalyticsbackend.model.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.budiyanto.petalytics.petalyticsbackend.TestcontainersConfiguration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
