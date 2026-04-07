package com.budiyanto.petalytics.petalyticsbackend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItem {

    @Id
    private UUID id;

    @Version
    private Integer version;

    @Column(name = "sku")
    private String sku;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private OrderItem(Order order, String sku, String name, BigDecimal price, int quantity) {
        this.id = UUID.randomUUID();
        this.order = order;
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public static OrderItem create(Order order, String sku, String name, BigDecimal price, int quantity) {
        if (order == null) {
            throw new IllegalArgumentException("Order item must be associated with an order");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Order item name cannot be null or blank");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Order item quantity must be greater than zero");
        }

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order item price must be greater than zero");
        }

        return new OrderItem(order, sku, name, price, quantity);
    }

}
