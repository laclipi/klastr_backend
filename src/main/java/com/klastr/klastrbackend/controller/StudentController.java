package com.klastr.klastrbackend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.klastr.klastrbackend.dto.CreateStudentRequest;
import com.klastr.klastrbackend.dto.StudentResponse;
import com.klastr.klastrbackend.service.StudentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tenants/{tenantId}/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // CREATE
    @PostMapping
    public ResponseEntity<StudentResponse> create(
            @PathVariable UUID tenantId,
            @Valid @RequestBody CreateStudentRequest request) {

        return ResponseEntity.ok(
                studentService.create(tenantId, request)
        );
    }

    // FIND BY ID
    @GetMapping("/{studentId}")
    public StudentResponse findById(
            @PathVariable UUID tenantId,
            @PathVariable UUID studentId) {

        return studentService.findById(tenantId, studentId);
    }

    // FIND BY ORGANIZATION
    @GetMapping("/organization/{organizationId}")
    public List<StudentResponse> findByOrganization(
            @PathVariable UUID tenantId,
            @PathVariable UUID organizationId) {

        return studentService.findByOrganization(tenantId, organizationId);
    }
}
