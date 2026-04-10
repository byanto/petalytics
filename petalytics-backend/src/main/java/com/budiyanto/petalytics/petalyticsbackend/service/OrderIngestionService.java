package com.budiyanto.petalytics.petalyticsbackend.service;

import com.budiyanto.petalytics.petalyticsbackend.domain.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.domain.Order;
import com.budiyanto.petalytics.petalyticsbackend.repository.OrderRepository;
import com.budiyanto.petalytics.petalyticsbackend.service.parser.MarketplaceCsvParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderIngestionService {

    private final OrderRepository orderRepository;
    private final Map<Marketplace, MarketplaceCsvParser> parserRegistry;

    public OrderIngestionService(OrderRepository orderRepository, List<MarketplaceCsvParser> parsers) {
        this.orderRepository = orderRepository;
        this.parserRegistry = parsers.stream()
                .collect(Collectors.toMap(MarketplaceCsvParser::getSupportedMarketplace, Function.identity()));
    }

    @Transactional
    public void ingest(InputStream inputStream, Marketplace marketplace) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }

        if (marketplace == null) {
            throw new IllegalArgumentException("Marketplace cannot be null");
        }

        MarketplaceCsvParser parser = parserRegistry.get(marketplace);
        if (parser == null) {
            throw new IllegalArgumentException("No CSV parser found for marketplace: " + marketplace);
        }

        List<Order> orders = parser.parse(inputStream);
        orderRepository.saveAll(orders);
    }
}
