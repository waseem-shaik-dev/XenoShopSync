package com.example.XenoShopSync.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers",
        indexes = {
                @Index(name = "idx_customer_tenant", columnList = "tenantId"),
                @Index(name = "idx_customer_shopifyid", columnList = "shopifyId")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long shopifyId;

    @Column(nullable = false)
    private String tenantId;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String currency;
    private Integer ordersCount;
    private String state;
    private String tags;
    private Double totalSpent;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    public void addAddress(Address addr) {
        addr.setCustomer(this);
        addresses.add(addr);
    }

    public void removeAddress(Address addr) {
        addresses.remove(addr);
        addr.setCustomer(null);
    }
}
