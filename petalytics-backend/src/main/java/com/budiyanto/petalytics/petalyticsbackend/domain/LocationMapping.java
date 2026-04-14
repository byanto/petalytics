package com.budiyanto.petalytics.petalyticsbackend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "location_mappings")
@Getter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
public class LocationMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false)
    private LocationType locationType;

    @Column(name = "raw_name", nullable = false)
    private String rawName;

    @Column(name = "standardized_name", nullable = false)
    private String standardizedName;

    public static LocationMapping create(LocationType type, String rawName, String standardizedName) {
        var mapping = new LocationMapping();
        mapping.locationType = type;
        mapping.rawName = rawName;
        mapping.standardizedName = standardizedName;
        return mapping;
    }

}
