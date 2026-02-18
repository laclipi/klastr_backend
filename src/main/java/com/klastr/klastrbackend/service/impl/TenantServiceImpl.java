package com.klastr.klastrbackend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.klastr.klastrbackend.domain.Tenant;
import com.klastr.klastrbackend.dto.CreateTenantRequest;
import com.klastr.klastrbackend.dto.TenantResponse;
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

    // 🔥 mapper privado (MUY PRO)
    private TenantResponse mapToResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getStatus(),
                tenant.getCreatedAt()
        );
    }

    @Override
    public TenantResponse create(CreateTenantRequest request) {

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

        Tenant saved = tenantRepository.save(tenant);

        return mapToResponse(saved);
    }

    @Override
    public TenantResponse findById(UUID id) {

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Tenant not found with id: " + id)
                );

        return mapToResponse(tenant);
    }

    @Override
    public List<TenantResponse> findAll() {

        return tenantRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
