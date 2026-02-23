package com.klastr.klastrbackend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.klastr.klastrbackend.dto.CreateUserRequest;
import com.klastr.klastrbackend.dto.UpdateUserRequest;
import com.klastr.klastrbackend.dto.UserResponse;
import com.klastr.klastrbackend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tenants/{tenantId}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // CREATE
    @PostMapping
    public ResponseEntity<UserResponse> create(
            @PathVariable UUID tenantId,
            @Valid @RequestBody CreateUserRequest request) {

        return ResponseEntity.ok(
                userService.create(tenantId, request)
        );
    }

    // FIND BY ID
    @GetMapping("/{userId}")
    public UserResponse findById(
            @PathVariable UUID tenantId,
            @PathVariable UUID userId) {

        return userService.findById(tenantId, userId);
    }

    // FIND BY ORGANIZATION
    @GetMapping("/organization/{organizationId}")
    public List<UserResponse> findByOrganization(
            @PathVariable UUID tenantId,
            @PathVariable UUID organizationId) {

        return userService.findByOrganization(tenantId, organizationId);
    }

    // UPDATE
    @PutMapping("/{userId}")
    public UserResponse update(
            @PathVariable UUID tenantId,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request) {

        return userService.update(tenantId, userId, request);
    }

    // SOFT DELETE (DEACTIVATE)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID tenantId,
            @PathVariable UUID userId) {

        userService.deactivate(tenantId, userId);
        return ResponseEntity.noContent().build();
    }
}
