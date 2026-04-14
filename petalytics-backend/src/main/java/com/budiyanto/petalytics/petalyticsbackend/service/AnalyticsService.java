package com.budiyanto.petalytics.petalyticsbackend.service;

import com.budiyanto.petalytics.petalyticsbackend.domain.Marketplace;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.ChannelSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.LocationSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalyticsService {

    private final OrderRepository orderRepository;

    public List<LocationSummaryDto> retrieveOrderSummaryByLocation(Marketplace marketplace, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findOrderSummaryByLocation(marketplace, startDate, endDate);
    }

    public List<ChannelSummaryDto> retrieveOrderSummaryByChannel(Marketplace marketplace, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findOrderSummaryByChannel(marketplace, startDate, endDate);
    }
}
