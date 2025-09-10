package com.example.XenoShopSync.service;


import com.example.XenoShopSync.entity.Product;
import com.example.XenoShopSync.entity.ProductVariant;
import com.example.XenoShopSync.entity.Tenant;
import com.example.XenoShopSync.repository.ProductRepository;
import com.example.XenoShopSync.repository.TenantRepository;
import com.example.XenoShopSync.utility.ShopifyClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final TenantRepository tenantRepository;
    private final ShopifyClient shopifyClient;

    public ProductService(ProductRepository productRepository,
                          TenantRepository tenantRepository,
                          ShopifyClient shopifyClient) {
        this.productRepository = productRepository;
        this.tenantRepository = tenantRepository;
        this.shopifyClient = shopifyClient;
    }


    public void scheduledSync() {
        List<Tenant> tenants = tenantRepository.findAll();
        for (Tenant tenant : tenants) {
            syncProductsFromShopify(tenant);
        }
    }

    @Transactional
    public void syncProductsFromShopify(Tenant tenant) {
        Map<String, Object> response = shopifyClient.get(tenant, "/products.json");
        if (response == null || !response.containsKey("products")) return;
        List<Map<String, Object>> products = (List<Map<String, Object>>) response.get("products");
        for (Map<String, Object> p : products) {
            upsertProduct(p, tenant.getTenantId());
        }
    }

    private void upsertProduct(Map<String, Object> p, String tenantId) {
        Long shopifyId = Long.valueOf(p.get("id").toString());

        Optional<Product> existingOpt =
                productRepository.findByTenantIdAndShopifyId(tenantId, shopifyId);

        Product product = existingOpt.orElseGet(() -> Product.builder()
                .shopifyId(shopifyId)
                .tenantId(tenantId)
                .build());

        product.setTitle((String) p.get("title"));
        product.setBodyHtml((String) p.get("body_html"));
        product.setVendor((String) p.get("vendor"));
        product.setProductType((String) p.get("product_type"));
        product.setCreatedAt(parseDate(p.get("created_at")));
        product.setUpdatedAt(parseDate(p.get("updated_at")));
        product.setPublishedAt(parseDate(p.get("published_at")));
        product.setHandle((String) p.get("handle"));
        product.setStatus((String) p.get("status"));
        product.setTags((String) p.get("tags"));

        Map<String, Object> image = (Map<String, Object>) p.get("image");
        if (image != null) {
            product.setImageSrc((String) image.get("src"));
        }

        List<Map<String, Object>> variants = (List<Map<String, Object>>) p.get("variants");
        if (variants != null) {
            product.getVariants().clear();
            for (Map<String, Object> v : variants) {
                ProductVariant variant = ProductVariant.builder()
                        .shopifyVariantId(v.get("id") != null ? Long.valueOf(v.get("id").toString()) : null)
                        .shopifyProductId(v.get("product_id") != null ? Long.valueOf(v.get("product_id").toString()) : null)
                        .tenantId(tenantId)
                        .title((String) v.get("title"))
                        .option1((String) v.get("option1"))
                        .option2((String) v.get("option2"))
                        .option3((String) v.get("option3"))
                        .price(toDouble(v.get("price")))
                        .position(toInt(v.get("position")))
                        .taxable(v.get("taxable") != null && Boolean.TRUE.equals(v.get("taxable")))
                        .sku((String) v.get("sku"))
                        .inventoryQuantity(toInt(v.get("inventory_quantity")))
                        .inventoryItemId(v.get("inventory_item_id") != null ? Long.valueOf(v.get("inventory_item_id").toString()) : null)
                        .requiresShipping(v.get("requires_shipping") != null && Boolean.TRUE.equals(v.get("requires_shipping")))
                        .build();
                product.addVariant(variant);
            }
        }

        productRepository.save(product);
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
}
