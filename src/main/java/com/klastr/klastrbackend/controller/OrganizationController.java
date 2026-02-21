package com.klastr.klastrbackend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.klastr.klastrbackend.dto.CreateOrganizationRequest;
import com.klastr.klastrbackend.dto.OrganizationResponse;
import com.klastr.klastrbackend.dto.UpdateOrganizationRequest;
import com.klastr.klastrbackend.service.OrganizationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tenants/{tenantId}/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<OrganizationResponse> create(
            @PathVariable UUID tenantId,
            @Valid @RequestBody CreateOrganizationRequest request) {

        return ResponseEntity.ok(
                organizationService.create(tenantId, request)
        );
    }

    @GetMapping
    public List<OrganizationResponse> findByTenant(
            @PathVariable UUID tenantId) {

        return organizationService.findByTenant(tenantId);
    }

    @GetMapping("/{organizationId}")
    public OrganizationResponse findById(
            @PathVariable UUID tenantId,
            @PathVariable UUID organizationId) {

        return organizationService.findById(tenantId, organizationId);
    }

    @PutMapping("/{organizationId}")
    public OrganizationResponse update(
            @PathVariable UUID tenantId,
            @PathVariable UUID organizationId,
            @Valid @RequestBody UpdateOrganizationRequest request) {

        return organizationService.update(tenantId, organizationId, request);
    }

    @DeleteMapping("/{organizationId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID tenantId,
            @PathVariable UUID organizationId) {

        organizationService.delete(tenantId, organizationId);
        return ResponseEntity.noContent().build();
    }
}
