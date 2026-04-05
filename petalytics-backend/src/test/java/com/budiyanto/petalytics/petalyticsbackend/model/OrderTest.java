package com.budiyanto.petalytics.petalyticsbackend.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.catchThrowable;

@DisplayName("Order Tests")
class OrderTest {

    @Nested
    @DisplayName("Create method")
    class Create {

        @Test
        @DisplayName("Given valid parameters, when createOrder, then return Order with correct properties")
        void given_validParameters_when_createOrder_then_returnOrder() {
            // Given
            String orderNo = "2601020X4SFPSA";
            String username = "testuser";
            LocalDateTime orderDate = LocalDateTime.now();
            LocalDateTime completedDate = orderDate.plusDays(5);
            String shippingProvince = "Sumatera Utara";
            String shippingCity = "Kota Medan";
            Marketplace marketplace = Marketplace.SHOPEE;

            // When
            Order order = Order.create(orderNo, marketplace, orderDate, username, shippingProvince, shippingCity, completedDate);

            // Then
            then(order).isNotNull();
            then(order.getId()).isNotNull();
            then(order.getOrderNo()).isEqualTo(orderNo);
            then(order.getMarketplace()).isEqualTo(marketplace);
            then(order.getOrderDate()).isEqualTo(orderDate);
            then(order.getUsername()).isEqualTo(username);
            then(order.getShippingProvince()).isEqualTo(shippingProvince);
            then(order.getShippingCity()).isEqualTo(shippingCity);
            then(order.getTotalQuantity()).isEqualTo(0);
            then(order.getTotalAmount()).isEqualTo(BigDecimal.ZERO);
            then(order.getCompletedDate()).isEqualTo(completedDate);
            then(order.getOrderItems().size()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("AddOrderItem method")
    class AddOrderItem {

        @Test
        @DisplayName("Given an order and valid order item parameters, when addOrderItem, then the order should contain the new item and totals should be updated")
        void given_orderAndValidOrderItemParameters_when_addOrderItem_then_orderShouldContainNewItemAndTotalsShouldBeUpdated() {
            // Given
            Order order = Order.create("2601020X4SFPSA", Marketplace.SHOPEE, LocalDateTime.now(), "user1", "Sumatera Utara", "Kota Medan", LocalDateTime.now().plusDays(5));

            // When
            OrderItem orderItem = order.addOrderItem("SKU 1", "Product A", new BigDecimal("10"), 2);

            // Then
            then(order.getOrderItems()).containsExactly(orderItem);
            then(order.getTotalQuantity()).isEqualTo(2);
            then(order.getTotalAmount()).isEqualTo(new BigDecimal("20"));
        }

        @Test
        @DisplayName("Given an existing order and a new order item, when addOrderItem, then the order should contain all order items and totals should be updated")
        void given_existingOrderAndNewOrderItem_when_addOrderItem_then_orderShouldContainAllItemsAndTotalsShouldBeUpdated() {
            // Given
            Order order = Order.create("2601020X4SFPSA", Marketplace.SHOPEE, LocalDateTime.now(), "user1", "Sumatera Utara", "Kota Medan", LocalDateTime.now().plusDays(5));
            order.addOrderItem("SKU 1", "Product A", new BigDecimal("10"), 2);

            // When
            order.addOrderItem("SKU 2", "Product B", new BigDecimal("30"), 10);

            // Then
            then(order.getOrderItems()).hasSize(2);
            then(order.getTotalQuantity()).isEqualTo(12);
            then(order.getTotalAmount()).isEqualTo(new BigDecimal("320"));
        }

    }
}
