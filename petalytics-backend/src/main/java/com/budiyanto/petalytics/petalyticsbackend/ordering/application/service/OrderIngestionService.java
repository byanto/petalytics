package com.budiyanto.petalytics.petalyticsbackend.ordering.application.service;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.budiyanto.petalytics.petalyticsbackend.ordering.application.port.in.IngestOrderUseCase;
import com.budiyanto.petalytics.petalyticsbackend.ordering.application.port.out.CsvParserPort;
import com.budiyanto.petalytics.petalyticsbackend.ordering.application.port.out.OrderRepositoryPort;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Order;

@Transactional(readOnly = true)
public class OrderIngestionService implements IngestOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final Map<Marketplace, CsvParserPort> parserRegistry;

    public OrderIngestionService(OrderRepositoryPort orderRepositoryPort, List<CsvParserPort> parsers) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.parserRegistry = parsers.stream()
                .collect(Collectors.toMap(CsvParserPort::getSupportedMarketplace, Function.identity()));
    }

    @Transactional
    public void ingest(InputStream inputStream, Marketplace marketplace) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }

        if (marketplace == null) {
            throw new IllegalArgumentException("Marketplace cannot be null");
        }

        CsvParserPort parser = parserRegistry.get(marketplace);
        if (parser == null) {
            throw new IllegalArgumentException("No CSV parser found for marketplace: " + marketplace);
        }

        List<Order> parsedOrders = parser.parse(inputStream);
        if (parsedOrders.isEmpty()) {
            return;
        }

        // Handle existing duplicate orders, remove them before save to database
        // 1. Extract all Order Numbers from the parsed CV
        List<String> parsedOrderNos = parsedOrders.stream().map(Order::getOrderNo).toList();

        // 2. Fetch existing Order Numbers from the DB in a single, fast batch query
        Set<String> existingOrderNos = new HashSet<>(orderRepositoryPort.findExistingOrderNosFromGivenList(parsedOrderNos));

        // 3. Filter out any orders that already exist in the database
        List<Order> ordersToSave = parsedOrders.stream()
                        .filter(order -> !existingOrderNos.contains(order.getOrderNo()))
                        .toList();

        orderRepositoryPort.saveAll(ordersToSave);
    }
}
