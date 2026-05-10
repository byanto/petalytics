package com.budiyanto.petalytics.petalyticsbackend.ordering.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.budiyanto.petalytics.petalyticsbackend.ordering.application.port.in.IngestOrderUseCase;
import com.budiyanto.petalytics.petalyticsbackend.ordering.application.service.OrderIngestionService;
import com.budiyanto.petalytics.petalyticsbackend.ordering.application.port.out.CsvParserPort;
import com.budiyanto.petalytics.petalyticsbackend.ordering.application.port.out.OrderRepositoryPort;

@Configuration
public class OrderingConfig {

    @Bean
    public IngestOrderUseCase ingestOrderUseCase(OrderRepositoryPort orderRepositoryPort, List<CsvParserPort> parsers) {
        return new OrderIngestionService(orderRepositoryPort, parsers);
    }

}
