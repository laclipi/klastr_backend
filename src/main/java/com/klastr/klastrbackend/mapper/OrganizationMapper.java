package com.klastr.klastrbackend.mapper;

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

        return OrganizationResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .tenantId(organization.getTenant().getId())
                .build();
    }
}
