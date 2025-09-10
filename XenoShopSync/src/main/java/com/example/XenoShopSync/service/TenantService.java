package com.example.XenoShopSync.service;

import com.example.XenoShopSync.dto.TenantRequestDto;
import com.example.XenoShopSync.dto.TenantResponseDto;
import com.example.XenoShopSync.entity.Tenant;
import com.example.XenoShopSync.mapper.TenantMapper;
import com.example.XenoShopSync.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    @Transactional
    public TenantResponseDto registerTenant(TenantRequestDto requestDto) {
        Tenant tenant = TenantMapper.toEntity(requestDto);
        Tenant savedTenant = tenantRepository.save(tenant);
        return TenantMapper.toResponseDto(savedTenant);
    }

    @Transactional(readOnly = true)
    public List<TenantResponseDto> getAllTenants() {
        return tenantRepository.findAll()
                .stream()
                .map(TenantMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public TenantResponseDto getTenantById(Long id) {
        return tenantRepository.findById(id)
                .map(TenantMapper::toResponseDto)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public TenantResponseDto getTenantByTenantId(String tenantId) {
        return tenantRepository.findByTenantId(tenantId)
                .map(TenantMapper::toResponseDto)
                .orElseThrow(() -> new RuntimeException("Tenant not found with tenantId: " + tenantId));
    }

    @Transactional
    public TenantResponseDto updateTenant(Long id, TenantRequestDto requestDto) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found with id: " + id));

        tenant.setTenantId(requestDto.tenantId());
        tenant.setShopifyBaseUrl(requestDto.shopifyBaseUrl());
        tenant.setAccessToken(requestDto.accessToken());
        tenant.setShopName(requestDto.shopName());

        Tenant updated = tenantRepository.save(tenant);
        return TenantMapper.toResponseDto(updated);
    }

    @Transactional
    public void deleteTenant(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new RuntimeException("Tenant not found with id: " + id);
        }
        tenantRepository.deleteById(id);
    }

    @Transactional
    public void deleteTenantByTenantId(String tenantId) {
        tenantRepository.findByTenantId(tenantId)
                .ifPresentOrElse(
                        tenantRepository::delete,
                        () -> { throw new RuntimeException("Tenant not found with tenantId: " + tenantId); }
                );
    }

    @Transactional(readOnly = true)
    public boolean existsByTenantId(String tenantId) {
        return tenantRepository.existsByTenantId(tenantId);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return tenantRepository.existsById(id);
    }
}
