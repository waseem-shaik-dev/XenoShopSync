package com.example.XenoShopSync.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tenants",
        indexes = {
                @Index(name = "idx_tenant_identifier", columnList = "tenantId", unique = true)
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tenantId;

    @Column(nullable = false)
    private String shopifyBaseUrl;

    @Column(nullable = false)
    private String accessToken;

    private String shopName;
}
