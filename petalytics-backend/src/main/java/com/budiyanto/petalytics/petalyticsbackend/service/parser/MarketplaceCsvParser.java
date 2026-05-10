package com.budiyanto.petalytics.petalyticsbackend.service.parser;

import java.io.InputStream;
import java.util.List;

import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Order;

public interface MarketplaceCsvParser {

    Marketplace getSupportedMarketplace();
    List<Order> parse(InputStream inputStream);

}
