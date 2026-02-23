package com.klastr.klastrbackend.service;

import java.util.List;
import java.util.UUID;

import com.klastr.klastrbackend.dto.CreateUserRequest;
import com.klastr.klastrbackend.dto.UpdateUserRequest;
import com.klastr.klastrbackend.dto.UserResponse;

public interface UserService {

    UserResponse create(UUID tenantId, CreateUserRequest request);

    UserResponse update(UUID tenantId, UUID userId, UpdateUserRequest request);

    UserResponse findById(UUID tenantId, UUID userId);

    List<UserResponse> findByOrganization(UUID tenantId, UUID organizationId);

    void deactivate(UUID tenantId, UUID userId);
}
