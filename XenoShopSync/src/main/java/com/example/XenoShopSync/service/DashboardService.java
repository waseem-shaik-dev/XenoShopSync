package com.example.XenoShopSync.service;

import com.example.XenoShopSync.dto.DashboardResponse;
import com.example.XenoShopSync.dto.TopCustomerDto;
import com.example.XenoShopSync.dto.TopProductDto;
import com.example.XenoShopSync.entity.Customer;
import com.example.XenoShopSync.entity.Order;
import com.example.XenoShopSync.entity.OrderLineItem;
import com.example.XenoShopSync.entity.Product;
import com.example.XenoShopSync.repository.CustomerRepository;
import com.example.XenoShopSync.repository.OrderRepository;
import com.example.XenoShopSync.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public DashboardResponse getDashboard(String tenantId, LocalDate from, LocalDate to) {

        if (to == null) {
            to = LocalDate.now();  // default = today
        }
        if (from == null) {
            from = to.minusDays(29); // default = last 30 days
        }

        // ---- 1) Compute date ranges ----
        long days = ChronoUnit.DAYS.between(from, to) + 1;
        LocalDate prevTo = from.minusDays(1);
        LocalDate prevFrom = prevTo.minusDays(days - 1);

        OffsetDateTime fromOdt = from.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime toOdt = to.plusDays(1).atStartOfDay().minusNanos(1).atOffset(ZoneOffset.UTC);

        OffsetDateTime prevFromOdt = prevFrom.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime prevToOdt = prevTo.plusDays(1).atStartOfDay().minusNanos(1).atOffset(ZoneOffset.UTC);

        // ---- 2) Load data from repositories ----
        List<Order> currentOrders = orderRepository.findByTenantIdAndCreatedAtBetween(tenantId, fromOdt, toOdt);
        List<Order> previousOrders = orderRepository.findByTenantIdAndCreatedAtBetween(tenantId, prevFromOdt, prevToOdt);

        List<Customer> allCustomers = customerRepository.findByTenantId(tenantId);
        List<Customer> newCustomers = customerRepository.findByTenantIdAndCreatedAtBetween(tenantId, fromOdt, toOdt);

        List<Product> allProducts = productRepository.findByTenantId(tenantId);

        // ---- 3) Totals and percent change ----
        double currentRevenue = currentOrders.stream().mapToDouble(Order::getTotalPrice).sum();
        double previousRevenue = previousOrders.stream().mapToDouble(Order::getTotalPrice).sum();

        int currentOrdersCount = currentOrders.size();
        int previousOrdersCount = previousOrders.size();

        int currentCustomersTotal = allCustomers.size();
        int newCustomersCount = newCustomers.size();
        int previousCustomersTotal = customerRepository.findByTenantIdAndCreatedAtBetween(tenantId, prevFromOdt, prevToOdt).size();

        int productsCount = allProducts.size();

        // ---- 4) Revenue trend (daily buckets) ----
        Map<LocalDate, Double> revenueByDay = new LinkedHashMap<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = from.plusDays(i);
            double sum = currentOrders.stream()
                    .filter(o -> o.getCreatedAt().toLocalDate().equals(date))
                    .mapToDouble(Order::getTotalPrice)
                    .sum();
            revenueByDay.put(date, sum);
        }

        // ---- 5) Orders by day ----
        Map<LocalDate, Long> ordersByDay = new LinkedHashMap<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = from.plusDays(i);
            long count = currentOrders.stream()
                    .filter(o -> o.getCreatedAt().toLocalDate().equals(date))
                    .count();
            ordersByDay.put(date, count);
        }

        // ---- 6) Top products ----
        Map<Long, TempProductAgg> productAgg = new HashMap<>();
        for (Order order : currentOrders) {
            for (OrderLineItem item : order.getLineItems()) {
                long productId = item.getProductId();
                TempProductAgg agg = productAgg.getOrDefault(productId, new TempProductAgg(productId));
                agg.title = item.getTitle();
                agg.unitPrice = item.getPrice();
                agg.quantitySold += item.getQuantity();
                agg.revenue += item.getPrice() * item.getQuantity();

                // fetch stock & image from product if available
                Product product = allProducts.stream()
                        .filter(p -> p.getShopifyId().equals(productId))
                        .findFirst().orElse(null);
                if (product != null) {
                    agg.stock = product.getVariants().stream()
                            .mapToInt(v -> Optional.ofNullable(v.getInventoryQuantity()).orElse(0))
                            .sum();
                    agg.imageSrc = product.getImageSrc();
                }
                productAgg.put(productId, agg);
            }
        }
        List<TopProductDto> topProducts = productAgg.values().stream()
                .map(agg -> new TopProductDto(
                        agg.productId,
                        agg.title,
                        agg.unitPrice,
                        agg.imageSrc,
                        agg.stock,
                        agg.quantitySold,
                        agg.revenue
                ))
                .sorted(Comparator.comparingDouble(TopProductDto::revenue).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // ---- 7) Top customers ----
        Map<Long, TempCustomerAgg> customerAgg = new HashMap<>();
        for (Order order : currentOrders) {
            Customer c = customerRepository.findByTenantIdAndShopifyId(order.getTenantId(), order.getShopifyCustomerId()).orElse(null);
            if (c != null) {
                TempCustomerAgg agg = customerAgg.getOrDefault(c.getId(), new TempCustomerAgg(c.getId(), c.getFirstName() + " " + c.getLastName(), c.getEmail()));
                agg.totalSpent += order.getTotalPrice();
                agg.ordersCount += 1;
                customerAgg.put(c.getId(), agg);
            }
        }
        List<TopCustomerDto> topCustomers = customerAgg.values().stream()
                .map(agg -> new TopCustomerDto(
                        agg.customerId,
                        agg.name,
                        agg.email,
                        agg.totalSpent,
                        agg.ordersCount
                ))
                .sorted(Comparator.comparingDouble(TopCustomerDto::totalSpent).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // ---- 8) Build response (record) ----
        return new DashboardResponse(
                tenantId,
                from,
                to,
                prevFrom,
                prevTo,
                currentRevenue,
                percentChange(currentRevenue, previousRevenue),
                currentOrdersCount,
                percentChange(currentOrdersCount, previousOrdersCount),
                currentCustomersTotal,
                newCustomersCount,
                percentChange(currentCustomersTotal, previousCustomersTotal),
                productsCount,
                revenueByDay,
                ordersByDay,
                topProducts,
                topCustomers
        );
    }

    private double percentChange(double current, double previous) {
        if (previous == 0.0) {
            return current == 0.0 ? 0.0 : 100.0;
        }
        return ((current - previous) / Math.abs(previous)) * 100.0;
    }

    // --- internal helper classes for aggregation ---
    private static class TempProductAgg {
        final long productId;
        String title;
        double unitPrice;
        String imageSrc;
        int stock;
        int quantitySold;
        double revenue;

        TempProductAgg(long productId) {
            this.productId = productId;
        }
    }

    private static class TempCustomerAgg {
        final long customerId;
        final String name;
        final String email;
        double totalSpent;
        int ordersCount;

        TempCustomerAgg(long customerId, String name, String email) {
            this.customerId = customerId;
            this.name = name;
            this.email = email;
        }
    }
}
