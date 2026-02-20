package com.klastr.klastrbackend.service;

import java.util.List;
import java.util.UUID;

import com.klastr.klastrbackend.dto.CreateOrganizationRequest;
import com.klastr.klastrbackend.dto.OrganizationResponse;

public interface OrganizationService {

    OrganizationResponse create(UUID tenantId, CreateOrganizationRequest request);

    List<OrganizationResponse> findByTenant(UUID tenantId);
}
