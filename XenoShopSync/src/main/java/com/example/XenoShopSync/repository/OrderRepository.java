package com.example.XenoShopSync.repository;


import com.example.XenoShopSync.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByTenantIdAndShopifyOrderId(String tenantId, Long shopifyOrderId);
}
