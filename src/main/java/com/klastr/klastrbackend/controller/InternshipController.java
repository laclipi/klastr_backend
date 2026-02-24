package com.klastr.klastrbackend.controller;

import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;
import com.klastr.klastrbackend.service.InternshipService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tenants/{tenantId}/internships")
public class InternshipController {

    private final InternshipService internshipService;

    public InternshipController(InternshipService internshipService) {
        this.internshipService = internshipService;
    }

    @PostMapping
    public ResponseEntity<InternshipResponse> create(
            @PathVariable UUID tenantId,
            @Valid @RequestBody CreateInternshipRequest request) {

        return ResponseEntity.ok(
                internshipService.create(tenantId, request));
    }
}