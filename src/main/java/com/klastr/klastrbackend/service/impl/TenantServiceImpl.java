package com.klastr.klastrbackend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.klastr.klastrbackend.domain.Tenant;
import com.klastr.klastrbackend.domain.TenantStatus;
import com.klastr.klastrbackend.dto.CreateTenantRequest;
import com.klastr.klastrbackend.dto.UpdateTenantRequest;
import com.klastr.klastrbackend.dto.TenantResponse;
import com.klastr.klastrbackend.exception.BusinessException;
import com.klastr.klastrbackend.exception.ResourceNotFoundException;
import com.klastr.klastrbackend.mapper.TenantMapper;
import com.klastr.klastrbackend.repository.TenantRepository;
import com.klastr.klastrbackend.service.TenantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    @Override
    public TenantResponse create(CreateTenantRequest request) {

        String normalizedName = request.getName().trim();

        if (tenantRepository.existsByName(normalizedName)) {
            throw new BusinessException(
                    "Tenant with this name already exists",
                    HttpStatus.CONFLICT
            );
        }

        Tenant tenant = tenantMapper.toEntity(request);
        tenant.setName(normalizedName);
        tenant.setStatus(TenantStatus.ACTIVE);

        return tenantMapper.toResponse(
                tenantRepository.save(tenant)
        );
    }

    @Override
    public TenantResponse update(UUID id, UpdateTenantRequest request) {

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Tenant not found with id: " + id)
                );

        String normalizedName = request.getName().trim();

        if (!tenant.getName().equals(normalizedName)
                && tenantRepository.existsByName(normalizedName)) {

            throw new BusinessException(
                    "Tenant with this name already exists",
                    HttpStatus.CONFLICT
            );
        }

        tenant.setName(normalizedName);

        return tenantMapper.toResponse(
                tenantRepository.save(tenant)
        );
    }

    @Override
    public TenantResponse findById(UUID id) {

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Tenant not found with id: " + id)
                );

        return tenantMapper.toResponse(tenant);
    }

    @Override
    public List<TenantResponse> findAll() {

        return tenantRepository.findAll()
                .stream()
                .map(tenantMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Tenant not found with id: " + id)
                );

        // Soft delete recomendado en SaaS
        tenant.setStatus(TenantStatus.SUSPENDED);

        tenantRepository.save(tenant);
    }
}
