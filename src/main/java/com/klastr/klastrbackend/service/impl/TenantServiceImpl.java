package com.klastr.klastrbackend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.klastr.klastrbackend.domain.Tenant;
import com.klastr.klastrbackend.dto.CreateTenantRequest;
import com.klastr.klastrbackend.exception.ResourceNotFoundException;
import com.klastr.klastrbackend.repository.TenantRepository;
import com.klastr.klastrbackend.service.TenantService;

@Service
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    public TenantServiceImpl(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public Tenant create(CreateTenantRequest request) {

        Tenant tenant = Tenant.builder()
                .name(request.getName())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        return tenantRepository.save(tenant);

    }

    @Override
    public Tenant findById(UUID id) {

        return tenantRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Tenant not found with id: " + id));
    }

    @Override
    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }
}
