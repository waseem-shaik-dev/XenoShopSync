package com.example.XenoShopSync.repository;


import com.example.XenoShopSync.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByTenantId(String tenantId);
    Optional<Order> findByTenantIdAndShopifyOrderId(String tenantId, Long shopifyOrderId);
    List<Order> findByTenantIdAndEmail(String tenantId, String email);
    List<Order> findByTenantIdAndCreatedAtAfter(String tenantId, OffsetDateTime isoDateTime);
    List<Order> findByTenantIdAndUpdatedAtAfter(String tenantId, OffsetDateTime isoDateTime);
    long countByTenantId(String tenantId);

}
