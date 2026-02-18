package com.klastr.klastrbackend.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.klastr.klastrbackend.domain.Tenant;
import com.klastr.klastrbackend.dto.CreateTenantRequest;
import com.klastr.klastrbackend.dto.UpdateTenantRequest;
import com.klastr.klastrbackend.dto.TenantResponse;
import com.klastr.klastrbackend.exception.BusinessException;
import com.klastr.klastrbackend.exception.ResourceNotFoundException;
import com.klastr.klastrbackend.mapper.TenantMapper;
import com.klastr.klastrbackend.repository.TenantRepository;
import com.klastr.klastrbackend.service.TenantService;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    public TenantServiceImpl(TenantRepository tenantRepository,
            TenantMapper tenantMapper) {
        this.tenantRepository = tenantRepository;
        this.tenantMapper = tenantMapper;
    }

    // CREATE
    @Override
    public TenantResponse create(CreateTenantRequest request) {

        if (tenantRepository.existsByName(request.getName())) {
            throw new BusinessException(
                    "Tenant with this name already exists",
                    HttpStatus.CONFLICT
            );
        }

        Tenant tenant = tenantMapper.toEntity(request);
        Tenant saved = tenantRepository.save(tenant);

        return tenantMapper.toResponse(saved);
    }

    //  UPDATE
    @Override
    public TenantResponse update(UUID id, UpdateTenantRequest request) {

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Tenant not found with id: " + id)
                );

        // evitar duplicados
        if (tenantRepository.existsByName(request.getName())
                && !tenant.getName().equals(request.getName())) {

            throw new BusinessException(
                    "Tenant with this name already exists",
                    HttpStatus.CONFLICT
            );
        }

        tenant.setName(request.getName());

        Tenant updated = tenantRepository.save(tenant);

        return tenantMapper.toResponse(updated);
    }

    //  FIND BY ID
    @Override
    public TenantResponse findById(UUID id) {

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Tenant not found with id: " + id)
                );

        return tenantMapper.toResponse(tenant);
    }

    // FIND ALL
    @Override
    public List<TenantResponse> findAll() {

        return tenantRepository.findAll()
                .stream()
                .map(tenantMapper::toResponse)
                .collect(Collectors.toList());
    }

    //  DELETE  
    @Override
    public void delete(UUID id) {

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Tenant not found with id: " + id)
                );

        tenantRepository.delete(tenant);
    }
}
