package com.example.XenoShopSync.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses",
        indexes = {
                @Index(name = "idx_address_tenant", columnList = "tenantId"),
                @Index(name = "idx_address_shopifyid", columnList = "shopifyAddressId")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shopify_address_id")
    private Long shopifyAddressId;

    @Column(nullable = false)
    private String tenantId;

    private String firstName;
    private String lastName;
    private String company;
    private String address1;
    private String address2;
    private String city;
    private String province;
    private String country;
    private String zip;
    private String phone;
    private Boolean isDefault;
    private String provinceCode;
    private String countryCode;
    private String countryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
