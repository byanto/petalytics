package com.budiyanto.petalytics.petalyticsbackend.ordering.infrastructure.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.budiyanto.petalytics.petalyticsbackend.ordering.application.port.out.OrderRepositoryPort;
import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Order;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostgresOrderRepository implements OrderRepositoryPort {

    private final SpringDataOrderRepository orderRepository;

    @Override
    public void saveAll(List<Order> orders) {
        orderRepository.saveAll(orders);
    }

    @Override
    public List<String> findExistingOrderNosFromGivenList(List<String> orderNos) {
        return orderRepository.findExistingOrderNosFromGivenList(orderNos);
    }

}
