package com.budiyanto.petalytics.petalyticsbackend.location.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.budiyanto.petalytics.petalyticsbackend.location.domain.model.LocationMapping;

import java.util.UUID;

@Repository
public interface SpringDataLocationMappingRepository extends JpaRepository<LocationMapping, UUID> {
}
