package com.example.XenoShopSync.mapper;

import com.example.XenoShopSync.dto.AddressDto;
import com.example.XenoShopSync.dto.CustomerDto;
import com.example.XenoShopSync.entity.Address;
import com.example.XenoShopSync.entity.Customer;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerMapper {

    public static CustomerDto toDto(Customer customer) {
        if (customer == null) return null;

        List<AddressDto> addresses = customer.getAddresses() != null
                ? customer.getAddresses().stream()
                .map(CustomerMapper::toDto)
                .collect(Collectors.toList())
                : List.of();

        return new CustomerDto(
                customer.getId(),
                customer.getShopifyId(),
                customer.getTenantId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getCurrency(),
                customer.getOrdersCount(),
                customer.getState(),
                customer.getTags(),
                customer.getTotalSpent(),
                customer.getCreatedAt(),
                customer.getUpdatedAt(),
                addresses
        );
    }

    public static AddressDto toDto(Address address) {
        if (address == null) return null;

        return new AddressDto(
                address.getId(),
                address.getTenantId(),
                address.getFirstName(),
                address.getLastName(),
                address.getCompany(),
                address.getAddress1(),
                address.getAddress2(),
                address.getCity(),
                address.getProvince(),
                address.getCountry(),
                address.getZip(),
                address.getPhone(),
                address.getIsDefault(),
                address.getProvinceCode(),
                address.getCountryCode(),
                address.getCountryName()
        );
    }
}
