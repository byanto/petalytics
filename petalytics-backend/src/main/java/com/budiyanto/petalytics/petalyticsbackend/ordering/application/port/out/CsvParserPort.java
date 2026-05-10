package com.budiyanto.petalytics.petalyticsbackend.ordering.application.port.out;

import java.io.InputStream;
import java.util.List;

import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Order;

public interface CsvParserPort {

    List<Order> parse(InputStream inputStream);
    Marketplace getSupportedMarketplace();
    
}
