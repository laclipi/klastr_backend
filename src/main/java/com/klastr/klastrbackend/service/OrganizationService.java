package com.klastr.klastrbackend.service;

import java.util.List;
import java.util.UUID;

import com.klastr.klastrbackend.dto.CreateOrganizationRequest;
import com.klastr.klastrbackend.dto.OrganizationResponse;
import com.klastr.klastrbackend.dto.UpdateOrganizationRequest;

public interface OrganizationService {

    /**
     * Creates a new organization inside the given tenant.
     *
     * @param tenantId tenant identifier
     * @param request organization creation payload
     * @return created organization response
     */
    OrganizationResponse create(UUID tenantId, CreateOrganizationRequest request);

    /**
     * Returns all organizations belonging to a tenant.
     *
     * @param tenantId tenant identifier
     * @return list of organizations
     */
    List<OrganizationResponse> findByTenant(UUID tenantId);

    /**
     * Returns a specific organization inside a tenant.
     *
     * @param tenantId tenant identifier
     * @param organizationId organization identifier
     * @return organization response
     */
    OrganizationResponse findById(UUID tenantId, UUID organizationId);

    /**
     * Updates an organization inside a tenant.
     *
     * @param tenantId tenant identifier
     * @param organizationId organization identifier
     * @param request update payload
     * @return updated organization
     */
    OrganizationResponse update(UUID tenantId, UUID organizationId, UpdateOrganizationRequest request);

    /**
     * Deletes an organization inside a tenant.
     *
     * @param tenantId tenant identifier
     * @param organizationId organization identifier
     */
    void delete(UUID tenantId, UUID organizationId);
}
