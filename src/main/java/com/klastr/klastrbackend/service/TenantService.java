package com.klastr.klastrbackend.service;

import java.util.List;
import java.util.UUID;

import com.klastr.klastrbackend.dto.CreateTenantRequest;
import com.klastr.klastrbackend.dto.TenantResponse;

public interface TenantService {

    TenantResponse create(CreateTenantRequest request);

    TenantResponse findById(UUID id);

    List<TenantResponse> findAll();
}
