package com.klastr.klastrbackend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.klastr.klastrbackend.dto.CreateOrganizationRequest;
import com.klastr.klastrbackend.dto.OrganizationResponse;
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

        OrganizationResponse response
                = organizationService.create(tenantId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<OrganizationResponse> findByTenant(
            @PathVariable UUID tenantId) {

        return organizationService.findByTenant(tenantId);
    }
}
