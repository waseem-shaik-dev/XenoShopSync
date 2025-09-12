package com.example.XenoShopSync.service;

import com.example.XenoShopSync.dto.OrderDto;
import com.example.XenoShopSync.entity.Address;
import com.example.XenoShopSync.entity.Order;
import com.example.XenoShopSync.entity.OrderLineItem;
import com.example.XenoShopSync.entity.Tenant;
import com.example.XenoShopSync.mapper.OrderMapper;
import com.example.XenoShopSync.repository.OrderRepository;
import com.example.XenoShopSync.repository.TenantRepository;
import com.example.XenoShopSync.utility.ShopifyClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final TenantRepository tenantRepository;
    private final ShopifyClient shopifyClient;

    public OrderService(OrderRepository orderRepository,
                        TenantRepository tenantRepository,
                        ShopifyClient shopifyClient) {
        this.orderRepository = orderRepository;
        this.tenantRepository = tenantRepository;
        this.shopifyClient = shopifyClient;
    }

    public void scheduledSync() {
        List<Tenant> tenants = tenantRepository.findAll();
        for (Tenant tenant : tenants) {
            syncOrdersFromShopify(tenant);
        }
    }

    @Transactional
    public void syncOrdersFromShopify(Tenant tenant) {
        Map<String, Object> response = shopifyClient.get(tenant, "/orders.json?status=any");
        if (response == null || !response.containsKey("orders")) return;
        List<Map<String, Object>> orders = (List<Map<String, Object>>) response.get("orders");
        for (Map<String, Object> o : orders) {
            upsertOrder(o, tenant.getTenantId());
        }
    }

    private void upsertOrder(Map<String, Object> o, String tenantId) {
        Long shopifyOrderId = Long.valueOf(o.get("id").toString());
        Optional<Order> existingOpt = orderRepository.findByTenantIdAndShopifyOrderId(tenantId, shopifyOrderId);

        Order order = existingOpt.orElseGet(() -> Order.builder()
                .shopifyOrderId(shopifyOrderId)
                .tenantId(tenantId)
                .totalPrice(0.0)
                .build());

        // 🔹 Core fields
        order.setEmail((String) o.get("email"));
        order.setPhone((String) o.get("phone"));
        order.setCreatedAt(parseDate(o.get("created_at")));
        order.setUpdatedAt(parseDate(o.get("updated_at")));
        order.setProcessedAt(parseDate(o.get("processed_at")));
        order.setCancelledAt(parseDate(o.get("cancelled_at")));
        order.setClosedAt(parseDate(o.get("closed_at")));

        order.setCurrency((String) o.get("currency"));
        order.setPresentmentCurrency((String) o.get("presentment_currency"));
        order.setTotalPrice(toDouble(o.get("total_price")));
        order.setSubtotalPrice(toDouble(o.get("subtotal_price")));
        order.setTotalDiscounts(toDouble(o.get("total_discounts")));
        order.setTotalTax(toDouble(o.get("total_tax")));
        order.setTotalLineItemsPrice(toDouble(o.get("total_line_items_price")));
        order.setTotalOutstanding(toDouble(o.get("total_outstanding")));
        order.setTotalTipReceived(toDouble(o.get("total_tip_received")));

        order.setFinancialStatus((String) o.get("financial_status"));
        order.setFulfillmentStatus((String) o.get("fulfillment_status"));
        order.setConfirmed(Boolean.TRUE.equals(o.get("confirmed")));
        order.setSourceName((String) o.get("source_name"));
        order.setPaymentGateway(extractPaymentGateway(o));
        order.setTags((String) o.get("tags"));
        order.setTestOrder(Boolean.TRUE.equals(o.get("test")));

        // 🔹 Shopify identifiers
        order.setAdminGraphqlApiId((String) o.get("admin_graphql_api_id"));
        order.setOrderNumber(toLong(o.get("order_number")));
        order.setName((String) o.get("name"));
        order.setConfirmationNumber((String) o.get("confirmation_number"));

        // 🔹 Addresses
        order.setShippingAddress(extractAddress((Map<String, Object>) o.get("shipping_address"), tenantId));
        order.setBillingAddress(extractAddress((Map<String, Object>) o.get("billing_address"), tenantId));

        // 🔹 Line items
        List<Map<String, Object>> lineItems = (List<Map<String, Object>>) o.get("line_items");
        if (lineItems != null) {

            order.getLineItems().clear();
            for (Map<String, Object> li : lineItems) {
                OrderLineItem lineItem = OrderLineItem.builder()
                        .tenantId(tenantId)
                        .shopifyLineItemId(toLong(li.get("id")))
                        .adminGraphqlApiId((String) li.get("admin_graphql_api_id"))
                        .title((String) li.get("title"))
                        .productId(toLong(li.get("product_id")))
                        .variantId(toLong(li.get("variant_id")))
                        .vendor((String) li.get("vendor"))
                        .quantity(toInt(li.get("quantity")))
                        .price(toDouble(li.get("price")))
                        .giftCard(Boolean.TRUE.equals(li.get("gift_card")))
                        .requiresShipping(Boolean.TRUE.equals(li.get("requires_shipping")))
                        .taxable(Boolean.TRUE.equals(li.get("taxable")))
                        .order(order)
                        .build();

                order.getLineItems().add(lineItem);
            }
        }

        orderRepository.save(order);
    }

    // -------------------------
    // Helpers
    // -------------------------

    private OffsetDateTime parseDate(Object o) {
        if (o == null) return null;
        try {
            return OffsetDateTime.parse(o.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Double toDouble(Object o) {
        if (o == null) return 0.0;
        try {
            return Double.parseDouble(o.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Integer toInt(Object o) {
        if (o == null) return 0;
        try {
            return Integer.parseInt(o.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private Long toLong(Object o) {
        if (o == null) return null;
        try {
            return Long.valueOf(o.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private String extractPaymentGateway(Map<String, Object> o) {
        try {
            List<String> gateways = (List<String>) o.get("payment_gateway_names");
            if (gateways != null && !gateways.isEmpty()) {
                return gateways.get(0);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private Address extractAddress(Map<String, Object> addrMap, String tenantId) {
        if (addrMap == null) return null;
        return Address.builder()
                .tenantId(tenantId)
                .shopifyAddressId(toLong(addrMap.get("id")))
                .firstName((String) addrMap.get("first_name"))
                .lastName((String) addrMap.get("last_name"))
                .company((String) addrMap.get("company"))
                .address1((String) addrMap.get("address1"))
                .address2((String) addrMap.get("address2"))
                .city((String) addrMap.get("city"))
                .province((String) addrMap.get("province"))
                .country((String) addrMap.get("country"))
                .zip((String) addrMap.get("zip"))
                .phone((String) addrMap.get("phone"))
                .provinceCode((String) addrMap.get("province_code"))
                .countryCode((String) addrMap.get("country_code"))
                .countryName((String) addrMap.get("country_name"))
                .isDefault(false)
                .build();
    }





    // 1. Total orders for a tenant
    public long getTotalOrders(String tenantId) {
        return orderRepository.countByTenantId(tenantId);
    }

    // 2. Total revenue for a tenant
    public double getTotalRevenue(String tenantId) {
        return orderRepository.sumTotalPriceByTenantId(tenantId).orElse(0.0);
    }

    // 3. Orders within a date range
    public List<Order> getOrdersByDateRange(String tenantId, OffsetDateTime start, OffsetDateTime end) {
        return orderRepository.findByTenantIdAndCreatedAtBetween(tenantId, start, end);
    }

    // 4. Daily/Monthly order counts (for charts/trends)
    public List<Object[]> getOrdersGroupedByDate(String tenantId) {
        return orderRepository.countOrdersGroupedByDate(tenantId);
    }

    // 5. Top 5 customers by total spend
    public List<Object[]> getTopCustomersBySpend(String tenantId, int limit) {
        return orderRepository.findTopCustomersBySpend(tenantId, limit);
    }

    // 6. Orders by financial status (paid, pending, refunded, etc.)
    public Map<String, Long> getOrdersByFinancialStatus(String tenantId) {
        return orderRepository.countOrdersByFinancialStatus(tenantId);
    }

    // 7. Orders by fulfillment status (fulfilled, partially fulfilled, unfulfilled)
    public Map<String, Long> getOrdersByFulfillmentStatus(String tenantId) {
        return orderRepository.countOrdersByFulfillmentStatus(tenantId);
    }

    // 8. Average order value
    public double getAverageOrderValue(String tenantId) {
        Double totalRevenue = orderRepository.sumTotalPriceByTenantId(tenantId).orElse(0.0);
        Long totalOrders = orderRepository.countByTenantId(tenantId);
        return totalOrders > 0 ? totalRevenue / totalOrders : 0.0;
    }


    // 9. Recent orders (latest 10 orders)
    public List<Order> getRecentOrders(String tenantId) {
        return orderRepository.findTop10ByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    // 10. Revenue trend (sum per day for a range)
    public List<Object[]> getRevenueTrend(String tenantId, OffsetDateTime start, OffsetDateTime end) {
        return orderRepository.sumRevenueGroupedByDate(tenantId, start, end);
    }



    // Get all orders for a tenant (⚠️ careful if there are many orders, prefer pagination)
    public List<Order> getAllOrders(String tenantId) {
        return orderRepository.findByTenantId(tenantId);
    }

    // Get paginated orders for a tenant
    public Page<Order> getOrdersPaged(String tenantId, Pageable pageable) {
        return orderRepository.findByTenantId(tenantId, pageable);
    }

    // Get a single order by internal DB ID
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // Get a single order by Shopify Order ID
    public Optional<Order> getOrderByShopifyId(String tenantId, Long shopifyOrderId) {
        return orderRepository.findByTenantIdAndShopifyOrderId(tenantId, shopifyOrderId);
    }

    // Get recent N orders (e.g., last 5)
    public List<Order> getRecentOrders(String tenantId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.findTopNByTenantIdOrderByCreatedAtDesc(tenantId, pageable);
    }





}
