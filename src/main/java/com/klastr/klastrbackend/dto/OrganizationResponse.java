package com.klastr.klastrbackend.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrganizationResponse {

    private UUID id;
    private String name;
    private UUID tenantId;
}
