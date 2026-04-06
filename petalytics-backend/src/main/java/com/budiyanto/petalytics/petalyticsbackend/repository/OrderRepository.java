package com.budiyanto.petalytics.petalyticsbackend.repository;

import com.budiyanto.petalytics.petalyticsbackend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

}
