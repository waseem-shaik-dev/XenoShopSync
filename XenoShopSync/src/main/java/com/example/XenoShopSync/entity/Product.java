package com.example.XenoShopSync.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products",
        indexes = {
                @Index(name = "idx_product_tenant", columnList = "tenantId"),
                @Index(name = "idx_product_shopifyid", columnList = "shopifyId")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long shopifyId;
    private String tenantId;

    private String title;
    @Column(length = 5000)
    private String bodyHtml;
    private String vendor;
    private String productType;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime publishedAt;
    private String handle;
    private String status;
    private String tags;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();

    private String imageSrc;

    public void addVariant(ProductVariant variant) {
        variant.setProduct(this);
        variants.add(variant);
    }

    public void removeVariant(ProductVariant v) {
        variants.remove(v);
        v.setProduct(null);
    }
}
