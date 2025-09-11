package com.example.XenoShopSync.repository;


import com.example.XenoShopSync.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTenantId(String tenantId);
    Optional<Product> findByTenantIdAndShopifyId(String tenantId, Long shopifyId);
    List<Product> findByTenantIdAndVendor(String tenantId, String vendor);
    List<Product> findByTenantIdAndProductType(String tenantId, String productType);
    List<Product> findByTenantIdAndTagsContaining(String tenantId, String tag);
    Long countByTenantId(String tenantId);
}
