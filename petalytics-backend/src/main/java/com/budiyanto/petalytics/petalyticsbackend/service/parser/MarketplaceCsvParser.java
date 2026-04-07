package com.budiyanto.petalytics.petalyticsbackend.service.parser;

import com.budiyanto.petalytics.petalyticsbackend.domain.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.domain.Order;

import java.io.InputStream;
import java.util.List;

public interface MarketplaceCsvParser {

    Marketplace getSupportedMarketplace();
    List<Order> parse(InputStream inputStream);

}
