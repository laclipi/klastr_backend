package com.klastr.klastrbackend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.klastr.klastrbackend.dto.CreateTenantRequest;
import com.klastr.klastrbackend.dto.TenantResponse;
import com.klastr.klastrbackend.dto.UpdateTenantRequest;
import com.klastr.klastrbackend.service.TenantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    // CREATE
    @PostMapping
    public TenantResponse create(@Valid @RequestBody CreateTenantRequest request) {
        return tenantService.create(request);
    }

    //  UPDATE (PUT)
    @PutMapping("/{id}")
    public TenantResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTenantRequest request) {

        return tenantService.update(id, request);
    }

    // GET by id
    @GetMapping("/{id}")
    public TenantResponse findById(@PathVariable UUID id) {
        return tenantService.findById(id);
    }

    // GET all
    @GetMapping
    public List<TenantResponse> findAll() {
        return tenantService.findAll();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {

        tenantService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
