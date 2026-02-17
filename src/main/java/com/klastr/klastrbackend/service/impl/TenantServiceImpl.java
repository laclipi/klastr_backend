package com.klastr.klastrbackend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.klastr.klastrbackend.domain.Tenant;
import com.klastr.klastrbackend.dto.CreateTenantRequest;
import com.klastr.klastrbackend.exception.BusinessException;
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

        // 🔥 VALIDACIÓN 1 — nombre vacío
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BusinessException(
                    "Tenant name cannot be empty",
                    HttpStatus.BAD_REQUEST
            );
        }

        // 🔥 VALIDACIÓN 2 — duplicados
        if (tenantRepository.existsByName(request.getName())) {
            throw new BusinessException(
                    "Tenant with this name already exists",
                    HttpStatus.CONFLICT
            );
        }

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
                        -> new ResourceNotFoundException("Tenant not found with id: " + id)
                );
    }

    @Override
    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }
}
