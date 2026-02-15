package com.klastr.klastrbackend.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.klastr.klastrbackend.domain.Tenant;
import com.klastr.klastrbackend.dto.CreateTenantRequest;

public interface TenantService {

    Tenant create(CreateTenantRequest request);

    Optional<Tenant> findById(UUID id);

    List<Tenant> findAll();
}