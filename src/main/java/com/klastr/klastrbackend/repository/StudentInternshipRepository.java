package com.klastr.klastrbackend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;
import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternshipStatus;

public interface StudentInternshipRepository
        extends JpaRepository<StudentInternship, UUID> {

    // -----------------------------
    // MULTI-TENANT SAFE QUERIES
    // -----------------------------

    Optional<StudentInternship> findByIdAndTenant_Id(
            UUID id,
            UUID tenantId);

    List<StudentInternship> findByTenant_IdAndStudent_Id(
            UUID tenantId,
            UUID studentId);

    List<StudentInternship> findByTenant_IdAndStatus(
            UUID tenantId,
            StudentInternshipStatus status);

    boolean existsByTenant_IdAndStudent_IdAndStatus(
            UUID tenantId,
            UUID studentId,
            StudentInternshipStatus status);
}