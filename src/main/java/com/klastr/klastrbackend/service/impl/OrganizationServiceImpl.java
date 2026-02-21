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

    // CREATE
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

        Organization saved = organizationRepository.save(organization);

        return organizationMapper.toResponse(saved);
    }

    // GET ALL BY TENANT
    @Override
    public List<OrganizationResponse> findByTenant(UUID tenantId) {

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

    // GET BY ID
    @Override
    public OrganizationResponse findById(UUID tenantId, UUID organizationId) {

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Organization not found with id: " + organizationId)
                );

        if (!organization.getTenant().getId().equals(tenantId)) {
            throw new ResourceNotFoundException(
                    "Organization does not belong to tenant: " + tenantId
            );
        }

        return organizationMapper.toResponse(organization);
    }

    // UPDATE
    @Override
    public OrganizationResponse update(UUID tenantId, UUID organizationId, UpdateOrganizationRequest request) {

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Organization not found with id: " + organizationId)
                );

        if (!organization.getTenant().getId().equals(tenantId)) {
            throw new ResourceNotFoundException(
                    "Organization does not belong to tenant: " + tenantId
            );
        }

        if (organizationRepository.existsByNameAndTenantId(request.getName(), tenantId)
                && !organization.getName().equals(request.getName())) {

            throw new BusinessException(
                    "Organization already exists in this tenant",
                    HttpStatus.CONFLICT
            );
        }

        organization.setName(request.getName());

        Organization updated = organizationRepository.save(organization);

        return organizationMapper.toResponse(updated);
    }

    // DELETE
    @Override
    public void delete(UUID tenantId, UUID organizationId) {

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Organization not found with id: " + organizationId)
                );

        if (!organization.getTenant().getId().equals(tenantId)) {
            throw new ResourceNotFoundException(
                    "Organization does not belong to tenant: " + tenantId
            );
        }

        organizationRepository.delete(organization);
    }
}
