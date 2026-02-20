package com.klastr.klastrbackend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.klastr.klastrbackend.domain.Organization;
import com.klastr.klastrbackend.domain.Tenant;
import com.klastr.klastrbackend.dto.CreateOrganizationRequest;
import com.klastr.klastrbackend.dto.OrganizationResponse;
import com.klastr.klastrbackend.exception.BusinessException;
import com.klastr.klastrbackend.exception.ResourceNotFoundException;
import com.klastr.klastrbackend.mapper.OrganizationMapper;
import com.klastr.klastrbackend.repository.OrganizationRepository;
import com.klastr.klastrbackend.repository.TenantRepository;
import com.klastr.klastrbackend.service.OrganizationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final TenantRepository tenantRepository;
    private final OrganizationMapper organizationMapper;

    @Override
    public OrganizationResponse create(UUID tenantId, CreateOrganizationRequest request) {

        // 1️⃣ Verificar que el tenant existe
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Tenant not found with id: " + tenantId)
                );

        // 2️⃣ Evitar duplicado dentro del mismo tenant
        if (organizationRepository.existsByNameAndTenantId(request.getName(), tenantId)) {
            throw new BusinessException(
                    "Organization already exists in this tenant",
                    HttpStatus.CONFLICT
            );
        }

        // 3️⃣ Crear organization
        Organization organization = organizationMapper.toEntity(request);
        organization.setTenant(tenant);

        Organization saved = organizationRepository.save(organization);

        return organizationMapper.toResponse(saved);
    }

    @Override
    public List<OrganizationResponse> findByTenant(UUID tenantId) {

        // Validar que el tenant existe
        if (!tenantRepository.existsById(tenantId)) {
            throw new ResourceNotFoundException(
                    "Tenant not found with id: " + tenantId
            );
        }

        return organizationRepository.findByTenantIdWithTenant(tenantId)
                .stream()
                .map(organizationMapper::toResponse)
                .toList();
    }
}
