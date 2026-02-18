package com.klastr.klastrbackend.service;

import java.util.List;
import java.util.UUID;

import com.klastr.klastrbackend.dto.CreateTenantRequest;
import com.klastr.klastrbackend.dto.TenantResponse;
import com.klastr.klastrbackend.dto.UpdateTenantRequest;

public interface TenantService {

    TenantResponse create(CreateTenantRequest request);

    TenantResponse update(UUID id, UpdateTenantRequest request);

    TenantResponse findById(UUID id);

    List<TenantResponse> findAll();

    void delete(UUID id);
}
