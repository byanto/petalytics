package com.budiyanto.petalytics.petalyticsbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_province", columnList = "shipping_province"),
        @Index(name = "idx_order_city", columnList = "shipping_city")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order {

    @Id
    private UUID id;

    @Version
    private Integer version;

    @Column(name = "order_no", unique = true, nullable = false)
    private String orderNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "marketplace", nullable = false)
    private Marketplace marketplace;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    private String username;

    @Column(name = "shipping_province", nullable = false)
    private String shippingProvince;

    @Column(name = "shipping_city", nullable = false)
    private String shippingCity;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    private Order(String orderNo, Marketplace marketplace, LocalDateTime orderDate, 
                  String username, String shippingProvince, String shippingCity, 
                  LocalDateTime completedDate) {
        this.id = UUID.randomUUID();
        this.orderNo = orderNo;
        this.marketplace = marketplace;
        this.orderDate = orderDate;
        this.username = username;
        this.shippingProvince = shippingProvince;
        this.shippingCity = shippingCity;
        this.totalQuantity = 0;
        this.totalAmount = BigDecimal.ZERO;
        this.completedDate = completedDate;
        this.orderItems = new ArrayList<>();
    }

    public static Order create(String orderNo, Marketplace marketplace,
                               LocalDateTime orderDate, String username,
                               String shippingProvince, String shippingCity,
                               LocalDateTime completedDate) {
        return new Order(orderNo, marketplace, orderDate, username, shippingProvince, shippingCity, completedDate);
    }

    public OrderItem addOrderItem(String sku, String name, BigDecimal price, int quantity) {
        OrderItem item = OrderItem.create(this, sku, name, price, quantity);
        this.orderItems.add(item);
        this.totalQuantity += quantity;
        var lineTotal = price.multiply(BigDecimal.valueOf(quantity));
        this.totalAmount = totalAmount.add(lineTotal);
        return item;
    }

}
