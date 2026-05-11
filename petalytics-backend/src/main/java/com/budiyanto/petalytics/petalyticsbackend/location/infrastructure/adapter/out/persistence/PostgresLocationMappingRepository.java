package com.budiyanto.petalytics.petalyticsbackend.location.infrastructure.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.budiyanto.petalytics.petalyticsbackend.location.application.port.out.LocationMappingRepositoryPort;
import com.budiyanto.petalytics.petalyticsbackend.location.domain.model.LocationMapping;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostgresLocationMappingRepository implements LocationMappingRepositoryPort {

    private final SpringDataLocationMappingRepository locationMappingRepository;

    @Override
    public List<LocationMapping> findAll() {
        return locationMappingRepository.findAll();
    }

}
