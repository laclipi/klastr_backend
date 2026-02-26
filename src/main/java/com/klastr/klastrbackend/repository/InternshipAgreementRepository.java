package com.klastr.klastrbackend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klastr.klastrbackend.com.klastr.klastrbackend.domain.internship.AgreementStatus;
import com.klastr.klastrbackend.com.klastr.klastrbackend.domain.internship.InternshipAgreement;

public interface InternshipAgreementRepository
                extends JpaRepository<InternshipAgreement, UUID> {

        // =================================================
        // MULTI-TENANT QUERIES
        // =================================================

        Optional<InternshipAgreement> findByIdAndTenant_Id(
                        UUID id,
                        UUID tenantId);

        List<InternshipAgreement> findByTenant_IdAndOrganization_Id(
                        UUID tenantId,
                        UUID organizationId);

        // =================================================
        // VALIDATION / BUSINESS RULES
        // =================================================

        boolean existsByOrganization_IdAndStatus(
                        UUID organizationId,
                        AgreementStatus status);
}