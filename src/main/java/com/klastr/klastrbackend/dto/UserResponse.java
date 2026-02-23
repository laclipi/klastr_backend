package com.klastr.klastrbackend.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String role;

    private String status;

    private UUID tenantId;

    private UUID organizationId;
}
