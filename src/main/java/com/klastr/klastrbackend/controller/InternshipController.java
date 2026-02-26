package com.klastr.klastrbackend.controller;

import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;
import com.klastr.klastrbackend.dto.internship.RegisterAttendanceRequest;
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
        // LIFECYCLE
        // -------------------------------------------------

        @PostMapping("/{internshipId}/approve")
        public ResponseEntity<InternshipResponse> approve(
                        @PathVariable UUID tenantId,
                        @PathVariable UUID internshipId) {

                return ResponseEntity.ok(
                                internshipService.approve(tenantId, internshipId));
        }

        @PostMapping("/{internshipId}/reject")
        public ResponseEntity<InternshipResponse> reject(
                        @PathVariable UUID tenantId,
                        @PathVariable UUID internshipId) {

                return ResponseEntity.ok(
                                internshipService.reject(tenantId, internshipId, null));
        }

        @PostMapping("/{internshipId}/activate")
        public ResponseEntity<InternshipResponse> activate(
                        @PathVariable UUID tenantId,
                        @PathVariable UUID internshipId) {

                return ResponseEntity.ok(
                                internshipService.activate(tenantId, internshipId));
        }

        @PostMapping("/{internshipId}/cancel")
        public ResponseEntity<InternshipResponse> cancel(
                        @PathVariable UUID tenantId,
                        @PathVariable UUID internshipId) {

                return ResponseEntity.ok(
                                internshipService.cancel(tenantId, internshipId, null));
        }

        @PostMapping("/{internshipId}/complete")
        public ResponseEntity<InternshipResponse> complete(
                        @PathVariable UUID tenantId,
                        @PathVariable UUID internshipId) {

                return ResponseEntity.ok(
                                internshipService.complete(tenantId, internshipId));
        }

        // -------------------------------------------------
        // ATTENDANCE
        // -------------------------------------------------

        @PostMapping("/{internshipId}/attendance")
        public ResponseEntity<Void> registerAttendance(
                        @PathVariable UUID tenantId,
                        @PathVariable UUID internshipId,
                        @Valid @RequestBody RegisterAttendanceRequest request) {

                internshipService.registerAttendance(tenantId, internshipId, request);

                return ResponseEntity.ok().build();
        }

        @PostMapping("/{internshipId}/weeks/{weekId}/submit")
        public ResponseEntity<Void> submitWeek(
                        @PathVariable UUID tenantId,
                        @PathVariable UUID internshipId,
                        @PathVariable UUID weekId) {

                internshipService.submitWeek(tenantId, internshipId, weekId);

                return ResponseEntity.ok().build();
        }

        @PostMapping("/{internshipId}/weeks/{weekId}/approve")
        public ResponseEntity<Void> approveWeek(
                        @PathVariable UUID tenantId,
                        @PathVariable UUID internshipId,
                        @PathVariable UUID weekId) {

                internshipService.approveWeek(tenantId, internshipId, weekId);

                return ResponseEntity.ok().build();
        }

        @PostMapping("/{internshipId}/weeks/{weekId}/reject")
        public ResponseEntity<Void> rejectWeek(
                        @PathVariable UUID tenantId,
                        @PathVariable UUID internshipId,
                        @PathVariable UUID weekId) {

                internshipService.rejectWeek(tenantId, internshipId, weekId, null);

                return ResponseEntity.ok().build();
        }

        @GetMapping("/{internshipId}/hours/approved")
        public ResponseEntity<Double> calculateApprovedHours(
                        @PathVariable UUID tenantId,
                        @PathVariable UUID internshipId) {

                return ResponseEntity.ok(
                                internshipService.calculateApprovedHours(tenantId, internshipId));
        }
}