package com.klastr.klastrbackend.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.klastr.klastrbackend.domain.tenant.Tenant;
import com.klastr.klastrbackend.dto.CreateTenantRequest;
import com.klastr.klastrbackend.dto.TenantResponse;

@Component
public class TenantMapper {

    public Tenant toEntity(CreateTenantRequest request) {

        return Tenant.builder()
                .name(request.getName())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getStatus(),
                tenant.getCreatedAt()
        );
    }
}
