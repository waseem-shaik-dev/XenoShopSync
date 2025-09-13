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


    @Transactional(readOnly = true)
    public List<TenantResponseDto> getAllTenants() {
        return tenantRepository.findAll()
                .stream()
                .map(TenantMapper::toResponseDto)
                .toList();
    }

}
