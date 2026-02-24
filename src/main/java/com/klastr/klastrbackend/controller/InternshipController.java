package com.klastr.klastrbackend.controller;

import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;
import com.klastr.klastrbackend.service.InternshipService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants/{tenantId}/internships")
public class InternshipController {

    private final InternshipService internshipService;

    public InternshipController(InternshipService internshipService) {
        this.internshipService = internshipService;
    }

    // -------------------------------------------------
    // CREATE
    // -------------------------------------------------
    @PostMapping
    public ResponseEntity<InternshipResponse> create(
            @PathVariable UUID tenantId,
            @Valid @RequestBody CreateInternshipRequest request) {

        return ResponseEntity.ok(
                internshipService.create(tenantId, request));
    }

    // -------------------------------------------------
    // FIND BY ID
    // -------------------------------------------------
    @GetMapping("/{internshipId}")
    public ResponseEntity<InternshipResponse> findById(
            @PathVariable UUID tenantId,
            @PathVariable UUID internshipId) {

        return ResponseEntity.ok(
                internshipService.findById(tenantId, internshipId));
    }

    // -------------------------------------------------
    // FIND BY STUDENT
    // -------------------------------------------------
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<InternshipResponse>> findByStudent(
            @PathVariable UUID tenantId,
            @PathVariable UUID studentId) {

        return ResponseEntity.ok(
                internshipService.findByStudent(tenantId, studentId));
    }

    // -------------------------------------------------
    // APPROVE
    // -------------------------------------------------
    @PostMapping("/{internshipId}/approve")
    public ResponseEntity<InternshipResponse> approve(
            @PathVariable UUID tenantId,
            @PathVariable UUID internshipId) {

        return ResponseEntity.ok(
                internshipService.approve(tenantId, internshipId));
    }

    // -------------------------------------------------
    // REJECT
    // -------------------------------------------------
    @PostMapping("/{internshipId}/reject")
    public ResponseEntity<InternshipResponse> reject(
            @PathVariable UUID tenantId,
            @PathVariable UUID internshipId) {

        return ResponseEntity.ok(
                internshipService.reject(tenantId, internshipId, null));
    }

    // -------------------------------------------------
    // ACTIVATE
    // -------------------------------------------------
    @PostMapping("/{internshipId}/activate")
    public ResponseEntity<InternshipResponse> activate(
            @PathVariable UUID tenantId,
            @PathVariable UUID internshipId) {

        return ResponseEntity.ok(
                internshipService.activate(tenantId, internshipId));
    }

    // -------------------------------------------------
    // CANCEL
    // -------------------------------------------------
    @PostMapping("/{internshipId}/cancel")
    public ResponseEntity<InternshipResponse> cancel(
            @PathVariable UUID tenantId,
            @PathVariable UUID internshipId) {

        return ResponseEntity.ok(
                internshipService.cancel(tenantId, internshipId, null));
    }

    // -------------------------------------------------
    // COMPLETE
    // -------------------------------------------------
    @PostMapping("/{internshipId}/complete")
    public ResponseEntity<InternshipResponse> complete(
            @PathVariable UUID tenantId,
            @PathVariable UUID internshipId) {

        return ResponseEntity.ok(
                internshipService.complete(tenantId, internshipId));
    }
}