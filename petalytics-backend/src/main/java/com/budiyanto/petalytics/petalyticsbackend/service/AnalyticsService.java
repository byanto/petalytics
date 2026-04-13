package com.budiyanto.petalytics.petalyticsbackend.service;

import com.budiyanto.petalytics.petalyticsbackend.domain.dto.ChannelSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.domain.dto.LocationSummaryDto;
import com.budiyanto.petalytics.petalyticsbackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalyticsService {

    private final OrderRepository orderRepository;

    public List<LocationSummaryDto> retrieveOrderSummaryByLocation() {
        return orderRepository.findOrderSummaryByLocation();
    }

    public List<ChannelSummaryDto> retrieveOrderSummaryByChannel() {
        return orderRepository.findOrderSummaryByChannel();
    }
}
