package com.budiyanto.petalytics.petalyticsbackend.analytics.infrastructure.adapter.in.web;

import com.budiyanto.petalytics.petalyticsbackend.analytics.application.port.in.RetrieveAnalyticsUseCase;
import com.budiyanto.petalytics.petalyticsbackend.analytics.application.service.AnalyticsService;
import com.budiyanto.petalytics.petalyticsbackend.analytics.domain.model.ChannelSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.analytics.domain.model.LocationSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Marketplace;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final RetrieveAnalyticsUseCase retrieveAnalyticsUseCase;

    @GetMapping("/location-summary")
    public ResponseEntity<List<LocationSummaryDto>> retrieveOrderSummaryByLocation(
            @RequestParam(required = false) Marketplace marketplace,
            @RequestParam(required = false)LocalDateTime startDate,
            @RequestParam(required = false)LocalDateTime endDate) {
        return ResponseEntity.ok(retrieveAnalyticsUseCase.retrieveOrderSummaryByLocation(marketplace, startDate, endDate));
    }

    @GetMapping("/channel-summary")
    public ResponseEntity<List<ChannelSummaryDto>> retrieveOrderSummaryByChannel(
            @RequestParam(required = false) Marketplace marketplace,
            @RequestParam(required = false)LocalDateTime startDate,
            @RequestParam(required = false)LocalDateTime endDate) {
        return ResponseEntity.ok(retrieveAnalyticsUseCase.retrieveOrderSummaryByChannel(marketplace, startDate, endDate));
    }

}
