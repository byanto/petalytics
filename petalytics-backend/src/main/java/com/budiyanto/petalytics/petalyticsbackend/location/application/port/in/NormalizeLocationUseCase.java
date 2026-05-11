package com.budiyanto.petalytics.petalyticsbackend.location.application.port.in;

public interface NormalizeLocationUseCase {

    String normalizeProvince(String rawProvince);
    String normalizeCity(String rawCity);

}
