package com.example.XenoShopSync.service;

import com.example.XenoShopSync.dto.CustomerDto;
import com.example.XenoShopSync.entity.Address;
import com.example.XenoShopSync.entity.Customer;
import com.example.XenoShopSync.entity.Tenant;
import com.example.XenoShopSync.mapper.CustomerMapper;
import com.example.XenoShopSync.repository.CustomerRepository;
import com.example.XenoShopSync.repository.TenantRepository;

import com.example.XenoShopSync.utility.ShopifyClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TenantRepository tenantRepository;
    private final ShopifyClient shopifyClient;

    public CustomerService(CustomerRepository customerRepository,
                           TenantRepository tenantRepository,
                           ShopifyClient shopifyClient) {
        this.customerRepository = customerRepository;
        this.tenantRepository = tenantRepository;
        this.shopifyClient = shopifyClient;
    }


    public void scheduledSync() {
        List<Tenant> tenants = tenantRepository.findAll();
        for (Tenant tenant : tenants) {
            syncCustomersFromShopify(tenant);
        }
    }

    @Transactional
    public void syncCustomersFromShopify(Tenant tenant) {
        Map<String, Object> response = shopifyClient.get(tenant, "/customers.json");
        if (response == null || !response.containsKey("customers")) return;
        List<Map<String, Object>> customers = (List<Map<String, Object>>) response.get("customers");
        for (Map<String, Object> c : customers) {
            upsertCustomer(c, tenant.getTenantId());
        }
    }

    private void upsertCustomer(Map<String, Object> c, String tenantId) {
        Long shopifyId = Long.valueOf(c.get("id").toString());
        Optional<Customer> existingOpt = customerRepository.findByTenantIdAndShopifyId(tenantId, shopifyId);
        Customer customer = existingOpt.orElseGet(() -> Customer.builder()
                .shopifyId(shopifyId)
                .tenantId(tenantId)
                .ordersCount(0)
                .totalSpent(0.0)
                .build());

        customer.setFirstName((String) c.get("first_name"));
        customer.setLastName((String) c.get("last_name"));
        customer.setEmail((String) c.get("email"));
        customer.setPhone((String) c.get("phone"));
        customer.setCurrency((String) c.get("currency"));
        customer.setOrdersCount(toInt(c.get("orders_count")));
        customer.setState((String) c.get("state"));
        customer.setTags((String) c.get("tags"));
        customer.setTotalSpent(toDouble(c.get("total_spent")));
        customer.setCreatedAt(parseDate(c.get("created_at")));
        customer.setUpdatedAt(parseDate(c.get("updated_at")));

        List<Map<String, Object>> addresses = (List<Map<String, Object>>) c.get("addresses");
        if (addresses != null) {
            customer.getAddresses().clear();
            for (Map<String, Object> addr : addresses) {
                Address a = Address.builder()
                        .shopifyAddressId(addr.get("id") != null ? Long.valueOf(addr.get("id").toString()) : null)
                        .tenantId(tenantId)
                        .firstName((String) addr.get("first_name"))
                        .lastName((String) addr.get("last_name"))
                        .company((String) addr.get("company"))
                        .address1((String) addr.get("address1"))
                        .address2((String) addr.get("address2"))
                        .city((String) addr.get("city"))
                        .province((String) addr.get("province"))
                        .country((String) addr.get("country"))
                        .zip((String) addr.get("zip"))
                        .phone((String) addr.get("phone"))
                        .isDefault(addr.get("default") != null && Boolean.TRUE.equals(addr.get("default")))
                        .provinceCode((String) addr.get("province_code"))
                        .countryCode((String) addr.get("country_code"))
                        .countryName((String) addr.get("country_name"))
                        .build();
                customer.addAddress(a);
            }
        }

        customerRepository.save(customer);
    }

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



    // ✅ Get all customers for a tenant
    public List<CustomerDto> getAllCustomers(String tenantId) {
        return customerRepository.findByTenantId(tenantId).stream()
                .map(CustomerMapper::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Get customer by database ID
    public Optional<CustomerDto> getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(CustomerMapper::toDto);
    }

    // ✅ Get customer by Shopify ID for a tenant
    public Optional<CustomerDto> getCustomerByShopifyId(String tenantId, Long shopifyId) {
        return customerRepository.findByTenantIdAndShopifyId(tenantId, shopifyId)
                .map(CustomerMapper::toDto);
    }

    // ✅ Get customers by email for a tenant
    public List<CustomerDto> getCustomersByEmail(String tenantId, String email) {
        return customerRepository.findByTenantIdAndEmail(tenantId, email).stream()
                .map(CustomerMapper::toDto)
                .collect(Collectors.toList());
    }

    // ✅ Get customers by tag for a tenant
    public List<CustomerDto> getCustomersByTag(String tenantId, String tag) {
        return customerRepository.findByTenantIdAndTagsContaining(tenantId, tag).stream()
                .map(CustomerMapper::toDto)
                .collect(Collectors.toList());

    }
}
