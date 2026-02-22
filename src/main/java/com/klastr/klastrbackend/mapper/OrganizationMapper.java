package com.klastr.klastrbackend.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.klastr.klastrbackend.domain.Organization;
import com.klastr.klastrbackend.dto.CreateOrganizationRequest;
import com.klastr.klastrbackend.dto.OrganizationResponse;

@Component
public class OrganizationMapper {

    public Organization toEntity(CreateOrganizationRequest request) {
        return Organization.builder()
                .name(request.getName())
                .build();
    }

    public OrganizationResponse toResponse(Organization organization) {

        UUID tenantId = organization.getTenant() != null
                ? organization.getTenant().getId()
                : null;

        return OrganizationResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .tenantId(tenantId)
                .build();
    }
}
