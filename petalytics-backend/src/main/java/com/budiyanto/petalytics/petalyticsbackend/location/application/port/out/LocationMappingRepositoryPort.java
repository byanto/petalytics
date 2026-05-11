package com.budiyanto.petalytics.petalyticsbackend.location.application.port.out;

import java.util.List;

import com.budiyanto.petalytics.petalyticsbackend.location.domain.model.LocationMapping;

public interface LocationMappingRepositoryPort {

    List<LocationMapping> findAll();

}
