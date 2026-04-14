package com.budiyanto.petalytics.petalyticsbackend.service;

import com.budiyanto.petalytics.petalyticsbackend.domain.LocationMapping;
import com.budiyanto.petalytics.petalyticsbackend.domain.LocationType;
import com.budiyanto.petalytics.petalyticsbackend.repository.LocationMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;


@DisplayName("Location Normalizer Service Unit Tests")
@ExtendWith(MockitoExtension.class)
class LocationNormalizerServiceTest {

    @Mock
    private LocationMappingRepository repository;

    @InjectMocks
    private LocationNormalizerService normalizer;

    @BeforeEach
    void setUp() {
        given(repository.findAll()).willReturn(List.of(
                LocationMapping.create(LocationType.PROVINCE, "D.I. Aceh", "Aceh"),
                LocationMapping.create(LocationType.PROVINCE, "East Java", "Jawa Timur"),
                LocationMapping.create(LocationType.PROVINCE, "Jakarta Province", "DKI Jakarta"),
                LocationMapping.create(LocationType.CITY, "Aceh Tenggara", "Kabupaten Aceh Tenggara"),
                LocationMapping.create(LocationType.CITY, "Ambon", "Kota Ambon"),
                LocationMapping.create(LocationType.CITY, "South Jakarta City", "Kota Jakarta Selatan")
        ));

        normalizer.loadMappingCache();
    }

    @ParameterizedTest(name = "Given ''{0}'', expects ''{1}''")
    @CsvSource({
            "Jawa Barat, Jawa Barat", // It shouldn't change any standard name
            "sumatera utara, Sumatera Utara", // It should change to title case
            "suMaTera uTara, Sumatera Utara", // It should change to title case
            "D.I. Aceh, Aceh", // It should change an invalid name to the standard name
            "BALI, Bali", // It should normalize uppercase province
            "East Java, Jawa Timur", // It should change the english name to the standard name
            "Jakarta Province, DKI Jakarta" // It should change the english name to the standard name
    })
    @DisplayName("Given variant province names, when normalizeProvince, then return standard name")
    void given_variantProvinceNames_when_normalizeProvince_then_returnsStandardName(String input, String expected) {
        then(normalizer.normalizeProvince(input)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "Given ''{0}'', expects ''{1}''")
    @CsvSource({
            "Kota Medan, Kota Medan", // It shouldn't change any standard name
            "kOTa MeDAn, Kota Medan", // It should change to title case
            "Kab. Deli Serdang, Kabupaten Deli Serdang", // It should change Kab. to Kabupaten
            "kab. malang, Kabupaten Malang", // It should capitalize city name
            "Aceh Tenggara, Kabupaten Aceh Tenggara", // It should append Kabupaten if necessary
            "Ambon, Kota Ambon", // It should append Kota if necessary,
            "South Jakarta City, Kota Jakarta Selatan" // It should change the english name to the standard name
    })
    @DisplayName("Given variant city names, when normalizeCity, then returns standard name")
    void given_variantCityNames_when_normalizeCity_then_returnsStandardName(String input, String expected) {
        then(normalizer.normalizeCity(input)).isEqualTo(expected);
    }
}
