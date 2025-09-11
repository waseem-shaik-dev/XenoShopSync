package com.example.XenoShopSync.repository;


import com.example.XenoShopSync.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByTenantId(String tenantId);

    Optional<Customer> findByTenantIdAndShopifyId(String tenantId, Long shopifyId);

    List<Customer> findByTenantIdAndEmail(String tenantId, String email);

    List<Customer> findByTenantIdAndTagsContaining(String tenantId, String tag);
}
