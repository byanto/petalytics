package com.budiyanto.petalytics.petalyticsbackend.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

@DisplayName("OrderItem tests")
class OrderItemTest {

    @Nested
    @DisplayName("Create method")
    class Create {

        private final Order order = Order.create("2601020X4SFPSA", Marketplace.SHOPEE, LocalDateTime.now(), "user1", "Sumatera Utara", "Kota Medan", LocalDateTime.now().plusDays(5));

        @Test
        @DisplayName("Given order and valid order item parameters, when createOrderItem, then a new order item should be created")
        void given_orderAndValidOrderItemParameters_when_createOrderItem_then_newOrderItemShouldBeCreated() {
            // Given
            String sku = "SKU 1";
            String name = "Product A";
            BigDecimal price = new BigDecimal("10");
            int quantity = 2;

            // When
            OrderItem orderItem = OrderItem.create(order, sku, name, price, quantity);

            // Then
            then(orderItem).isNotNull();
            then(orderItem.getId()).isNotNull();
            then(orderItem.getSku()).isEqualTo(sku);
            then(orderItem.getName()).isEqualTo(name);
            then(orderItem.getPrice()).isEqualTo(price);
            then(orderItem.getQuantity()).isEqualTo(quantity);
        }

        @Test
        @DisplayName("Given an order item with zero or negative quantity, when createOrderItem, then throw IllegalArgumentException")
        void given_orderItemWithZeroOrNegativeQuantity_when_createOrderItem_then_throwIllegalArgumentException() {
            // When
            Throwable thrown = catchThrowable(() -> OrderItem.create(order, "SKU 1", "Product A", new BigDecimal("10"), 0));

            // Then
            then(thrown)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Order item quantity must be greater than zero");
        }

        @Test
        @DisplayName("Given an order item with zero or negative price, when createOrderItem, then throw IllegalArgumentException")
        void given_orderItemWithZeroOrNegativePrice_when_createOrderItem_then_throwIllegalArgumentException() {
            // When
            Throwable thrown = catchThrowable(() -> OrderItem.create(order, "SKU 1", "Product A", new BigDecimal("0"), 5));

            // Then
            then(thrown)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Order item price must be greater than zero");
        }

        @Test
        @DisplayName("Given a null order, when createOrderItem, then throw IllegalArgumentException")
        void given_nullOrder_when_createOrderItem_then_throwIllegalArgumentException() {
            // Given
            Order nullOrder = null;

            // When
            Throwable thrown = catchThrowable(() -> OrderItem.create(nullOrder, "SKU 1", "Product A", new BigDecimal("0"), 5));

            // Then
            then(thrown)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Order item must be associated with an order");
        }

    }

}
