package com.klastr.klastrbackend.service;

import java.util.List;
import java.util.UUID;

import com.klastr.klastrbackend.dto.CreateOrganizationRequest;
import com.klastr.klastrbackend.dto.OrganizationResponse;
import com.klastr.klastrbackend.dto.UpdateOrganizationRequest;

public interface OrganizationService {

    OrganizationResponse create(UUID tenantId, CreateOrganizationRequest request);

    List<OrganizationResponse> findByTenant(UUID tenantId);

    OrganizationResponse findById(UUID tenantId, UUID organizationId);

    OrganizationResponse update(UUID tenantId, UUID organizationId, UpdateOrganizationRequest request);

    void delete(UUID tenantId, UUID organizationId);
}
