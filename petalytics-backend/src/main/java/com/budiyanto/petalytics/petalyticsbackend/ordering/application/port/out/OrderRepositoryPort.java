package com.budiyanto.petalytics.petalyticsbackend.ordering.application.port.out;

import java.util.List;

import com.budiyanto.petalytics.petalyticsbackend.ordering.domain.model.Order;

public interface OrderRepositoryPort {
    
    void saveAll(List<Order> orders);

    List<String> findExistingOrderNosFromGivenList(List<String> orderNos);
}
