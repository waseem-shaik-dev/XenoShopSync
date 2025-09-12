package com.example.XenoShopSync.repository;


import com.example.XenoShopSync.entity.Order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByTenantId(String tenantId);

    Page<Order> findByTenantId(String tenantId, Pageable pageable);

    Optional<Order> findByTenantIdAndShopifyOrderId(String tenantId, Long shopifyOrderId);

    @Query("SELECT o FROM Order o WHERE o.tenantId = :tenantId ORDER BY o.createdAt DESC")
    List<Order> findTopNByTenantIdOrderByCreatedAtDesc(@Param("tenantId") String tenantId, Pageable pageable);


    long countByTenantId(String tenantId);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.tenantId = :tenantId")
    Optional<Double> sumTotalPriceByTenantId(@Param("tenantId") String tenantId);


    List<Order> findByTenantIdAndCreatedAtBetween(String tenantId, OffsetDateTime start, OffsetDateTime end);

    @Query("SELECT DATE(o.createdAt), COUNT(o) FROM Order o WHERE o.tenantId = :tenantId GROUP BY DATE(o.createdAt) ORDER BY DATE(o.createdAt)")
    List<Object[]> countOrdersGroupedByDate(@Param("tenantId") String tenantId);

    @Query("SELECT o.email, SUM(o.totalPrice) FROM Order o WHERE o.tenantId = :tenantId GROUP BY o.email ORDER BY SUM(o.totalPrice) DESC LIMIT :limit")
    List<Object[]> findTopCustomersBySpend(@Param("tenantId") String tenantId, @Param("limit") int limit);

    @Query("SELECT o.financialStatus, COUNT(o) FROM Order o WHERE o.tenantId = :tenantId GROUP BY o.financialStatus")
    Map<String, Long> countOrdersByFinancialStatus(@Param("tenantId") String tenantId);

    @Query("SELECT o.fulfillmentStatus, COUNT(o) FROM Order o WHERE o.tenantId = :tenantId GROUP BY o.fulfillmentStatus")
    Map<String, Long> countOrdersByFulfillmentStatus(@Param("tenantId") String tenantId);

    List<Order> findTop10ByTenantIdOrderByCreatedAtDesc(String tenantId);

    @Query("SELECT DATE(o.createdAt), SUM(o.totalPrice) FROM Order o WHERE o.tenantId = :tenantId AND o.createdAt BETWEEN :start AND :end GROUP BY DATE(o.createdAt) ORDER BY DATE(o.createdAt)")
    List<Object[]> sumRevenueGroupedByDate(@Param("tenantId") String tenantId,
                                           @Param("start") OffsetDateTime start,
                                           @Param("end") OffsetDateTime end);


}
