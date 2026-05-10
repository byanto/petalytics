package com.budiyanto.petalytics.petalyticsbackend.ordering.application.port.in;

import java.io.InputStream;

import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Marketplace;

public interface IngestOrderUseCase {

    void ingest(InputStream inputStream, Marketplace marketplace);

}
