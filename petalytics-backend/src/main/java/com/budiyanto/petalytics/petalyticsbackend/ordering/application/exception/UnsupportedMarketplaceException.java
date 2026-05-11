package com.budiyanto.petalytics.petalyticsbackend.ordering.application.exception;

import java.util.List;

import lombok.Getter;

@Getter
public class UnsupportedMarketplaceException extends RuntimeException {

    private final String marketplace;
    private final List<String> supportedMarketplaces;

    public UnsupportedMarketplaceException(String marketplace, List<String> supportedMarketplaces) {
        super(String.format("Unsupported marketplace: '%s'. Supported marketplaces: %s", marketplace, supportedMarketplaces));
        this.marketplace = marketplace;
        this.supportedMarketplaces = supportedMarketplaces; 
    }
}
