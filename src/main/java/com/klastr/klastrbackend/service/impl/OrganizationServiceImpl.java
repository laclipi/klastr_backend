package com.klastr.klastrbackend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.klastr.klastrbackend.domain.Organization;
import com.klastr.klastrbackend.domain.Tenant;
import com.klastr.klastrbackend.dto.CreateOrganizationRequest;
import com.klastr.klastrbackend.dto.OrganizationResponse;
import com.klastr.klastrbackend.dto.UpdateOrganizationRequest;
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

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Tenant not found with id: " + tenantId)
                );

        if (organizationRepository.existsByNameAndTenantId(request.getName(), tenantId)) {
            throw new BusinessException(
                    "Organization already exists in this tenant",
                    HttpStatus.CONFLICT
            );
        }

        Organization organization = organizationMapper.toEntity(request);
        organization.setTenant(tenant);

        return organizationMapper.toResponse(
                organizationRepository.save(organization)
        );
    }

    @Override
    public List<OrganizationResponse> findByTenant(UUID tenantId) {

        if (!tenantRepository.existsById(tenantId)) {
            throw new ResourceNotFoundException("Tenant not found with id: " + tenantId);
        }

        return organizationRepository.findByTenantIdWithTenant(tenantId)
                .stream()
                .map(organizationMapper::toResponse)
                .toList();
    }

    @Override
    public OrganizationResponse findById(UUID tenantId, UUID organizationId) {

        Organization organization = organizationRepository
                .findByIdAndTenantId(organizationId, tenantId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Organization not found in tenant")
                );

        return organizationMapper.toResponse(organization);
    }

    @Override
    public OrganizationResponse update(UUID tenantId, UUID organizationId, UpdateOrganizationRequest request) {

        Organization organization = organizationRepository
                .findByIdAndTenantId(organizationId, tenantId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Organization not found in tenant")
                );

        if (!organization.getName().equals(request.getName())
                && organizationRepository.existsByNameAndTenantId(request.getName(), tenantId)) {

            throw new BusinessException(
                    "Organization already exists in this tenant",
                    HttpStatus.CONFLICT
            );
        }

        organization.setName(request.getName());

        return organizationMapper.toResponse(
                organizationRepository.save(organization)
        );
    }

    @Override
    public void delete(UUID tenantId, UUID organizationId) {

        Organization organization = organizationRepository
                .findByIdAndTenantId(organizationId, tenantId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Organization not found in tenant")
                );

        organizationRepository.delete(organization);
    }
}
