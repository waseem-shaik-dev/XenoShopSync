package com.example.XenoShopSync.repository;


import com.example.XenoShopSync.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByTenantIdAndShopifyId(String tenantId, Long shopifyId);
}
