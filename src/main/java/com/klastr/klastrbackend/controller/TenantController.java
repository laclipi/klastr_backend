package com.klastr.klastrbackend.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.klastr.klastrbackend.domain.Tenant;
import com.klastr.klastrbackend.dto.CreateTenantRequest;
import com.klastr.klastrbackend.service.TenantService;

@RestController
@RequestMapping("/api/tenants")
@CrossOrigin
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    public ResponseEntity<Tenant> create(@RequestBody CreateTenantRequest request) {

        Tenant created = tenantService.create(request);

        return ResponseEntity
                .created(URI.create("/api/tenants/" + created.getId()))
                .body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getById(@PathVariable UUID id) {

        Tenant tenant = tenantService.findById(id);

        return ResponseEntity.ok(tenant);
    }

    @GetMapping
    public ResponseEntity<List<Tenant>> list() {

        return ResponseEntity.ok(tenantService.findAll());
    }
}
