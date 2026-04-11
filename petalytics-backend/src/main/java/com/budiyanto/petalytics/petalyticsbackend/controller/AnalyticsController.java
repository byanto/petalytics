package com.budiyanto.petalytics.petalyticsbackend.controller;

import com.budiyanto.petalytics.petalyticsbackend.domain.dto.ChannelSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.LocationSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/location-summary")
    public ResponseEntity<List<LocationSummaryDto>> retrieveOrderSummaryByLocation() {
        return ResponseEntity.ok(analyticsService.retrieveOrderSummaryByLocation());
    }

    @GetMapping("/channel-summary")
    public ResponseEntity<List<ChannelSummaryDto>> retrieveOrderSummaryByChannel() {
        return ResponseEntity.ok(analyticsService.retrieveOrderSummaryByChannel());
    }

}
