package com.example.XenoShopSync.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_variants",
        indexes = {
                @Index(name = "idx_variant_tenant", columnList = "tenantId"),
                @Index(name = "idx_variant_shopifyid", columnList = "shopifyVariantId")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long shopifyVariantId;
    private Long shopifyProductId;
    private String tenantId;

    private String title;
    private String option1;
    private String option2;
    private String option3;
    private Double price;
    private Integer position;
    private Boolean taxable;
    private String sku;
    private Integer inventoryQuantity;
    private Long inventoryItemId;
    private Boolean requiresShipping;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}
