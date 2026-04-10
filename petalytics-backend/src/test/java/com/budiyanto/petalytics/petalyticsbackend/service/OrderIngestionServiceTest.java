package com.budiyanto.petalytics.petalyticsbackend.service;

import com.budiyanto.petalytics.petalyticsbackend.domain.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.domain.Order;
import com.budiyanto.petalytics.petalyticsbackend.domain.OrderItem;
import com.budiyanto.petalytics.petalyticsbackend.repository.OrderRepository;
import com.budiyanto.petalytics.petalyticsbackend.service.parser.MarketplaceCsvParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Ingestion Service Tests")
class OrderIngestionServiceTest {

    @Mock
    private MarketplaceCsvParser shopeeCsvParser;

    @Mock
    private OrderRepository orderRepository;

    private OrderIngestionService orderIngestionService;

    @Captor
    private ArgumentCaptor<List<Order>> orderListCaptor;

    @BeforeEach
    void setUp() {
        // We stub the marketplace FIRST, then manually inject it into the real constructor!
        given(shopeeCsvParser.getSupportedMarketplace()).willReturn(Marketplace.SHOPEE);
        orderIngestionService = new OrderIngestionService(orderRepository, List.of(shopeeCsvParser));
    }

    @Test
    @DisplayName("Given a valid CSV input stream, when ingest, then parses and saves orders")
    void given_validCsvInputStream_when_ingest_then_parsesAndSavesOrders() {
        // Given
        InputStream inputStream = new ByteArrayInputStream("dummy csv data".getBytes(StandardCharsets.UTF_8));

        // We mock the domain object that the parser should return
        String orderNo = "2601020X4SFPSA";
        Marketplace marketplace = Marketplace.SHOPEE;
        LocalDateTime orderDate = LocalDateTime.now();
        String username = "user1";
        String shippingProvince = "Sumatera Utara";
        String shippingCity = "Kota Medan";
        LocalDateTime completedDate = orderDate.plusDays(5);

        Order mockOrder = Order.create(orderNo, marketplace, orderDate, username,
                shippingProvince, shippingCity, completedDate);
        mockOrder.addOrderItem("SKU 1", "Product A", new BigDecimal("10"), 2);
        mockOrder.addOrderItem("SKU 2", "Product B", new BigDecimal("25"), 3);

        given(shopeeCsvParser.parse(any(InputStream.class))).willReturn(List.of(mockOrder));

        // When
        orderIngestionService.ingest(inputStream, Marketplace.SHOPEE);

        // Then
        verify(orderRepository, times(1)).saveAll(orderListCaptor.capture());
        List<Order> savedOrders = orderListCaptor.getValue();
        
        then(savedOrders).hasSize(1);
        Order savedOrder = savedOrders.getFirst();

        then(savedOrder.getOrderNo()).isEqualTo(orderNo);
        then(savedOrder.getMarketplace()).isEqualTo(marketplace);
        then(savedOrder.getShippingProvince()).isEqualTo(shippingProvince);
        then(savedOrder.getShippingCity()).isEqualTo(shippingCity);
        then(savedOrder.getOrderDate()).isEqualTo(orderDate);
        then(savedOrder.getUsername()).isEqualTo(username);
        then(savedOrder.getCompletedDate()).isEqualTo(completedDate);
        then(savedOrder.getOrderItems()).hasSize(2);
        then(savedOrder.getOrderItems().getFirst().getSku()).isEqualTo("SKU 1");
        then(savedOrder.getTotalAmount()).isEqualTo(new BigDecimal("95"));
        then(savedOrder.getTotalQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("Given a null Marketplace, when ingest, then throws IllegalArgumentException")
    void given_nullMarketplace_when_ingest_then_throwsIllegalArgumentException() {
        // Given
        InputStream inputStream = new ByteArrayInputStream("dummy csv data".getBytes(StandardCharsets.UTF_8));

        // When & Then
        thenThrownBy(() -> orderIngestionService.ingest(inputStream, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Marketplace cannot be null");
    }

    @Test
    @DisplayName("Given an unsupported Marketplace, when ingest, then throws IllegalArgumentException")
    void given_unsupportedMarketplace_when_ingest_then_throwsIllegalArgumentException() {
        // Given
        InputStream inputStream = new ByteArrayInputStream("dummy csv data".getBytes(StandardCharsets.UTF_8));

        // When & Then
        // Since we only registered the Shopee parser in setUp(), passing TIKTOK will yield a null parser.
        thenThrownBy(() -> orderIngestionService.ingest(inputStream, Marketplace.TIKTOK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No CSV parser found for marketplace: TIKTOK");
    }

    @Test
    @DisplayName("Given a null InputStream, when ingest, then throws IllegalArgumentException")
    void given_nullInputStream_when_ingest_then_throwsIllegalArgumentException() {
        // When & Then
        thenThrownBy(() -> orderIngestionService.ingest(null, Marketplace.SHOPEE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("InputStream cannot be null");
    }

}