package com.klastr.klastrbackend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.v.AgreementStatus;
import com.klastr.klastrbackend.com.klastr.klastrbackend.domain.internship.InternshipAgreement;
import com.klastr.klastrbackend.domain.tenant.Tenant;
import com.klastr.klastrbackend.dto.internship.AgreementResponse;
import com.klastr.klastrbackend.dto.internship.CreateAgreementRequest;
import com.klastr.klastrbackend.mapper.AgreementMapper;
import com.klastr.klastrbackend.repository.InternshipAgreementRepository;
import com.klastr.klastrbackend.repository.OrganizationRepository;
import com.klastr.klastrbackend.repository.TenantRepository;
import com.klastr.klastrbackend.service.AgreementService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AgreementServiceImpl implements AgreementService {

    private final InternshipAgreementRepository agreementRepository;
    private final OrganizationRepository organizationRepository;
    private final TenantRepository tenantRepository;
    private final AgreementMapper agreementMapper;

    // =================================================
    // CREACIÓN
    // =================================================

    @Override
    public AgreementResponse create(UUID tenantId, CreateAgreementRequest request) {

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found"));

        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        InternshipAgreement agreement = agreementMapper.toEntity(request, organization);

        agreement.setTenant(tenant);
        agreement.setStatus(AgreementStatus.DRAFT);

        InternshipAgreement saved = agreementRepository.save(agreement);

        return agreementMapper.toResponse(saved);
    }

    // =================================================
    // CONSULTA
    // =================================================

    @Override
    @Transactional(readOnly = true)
    public AgreementResponse findById(UUID tenantId, UUID agreementId) {

        InternshipAgreement agreement = agreementRepository
                .findByIdAndTenant_Id(agreementId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Agreement not found"));

        return agreementMapper.toResponse(agreement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgreementResponse> findByOrganization(UUID tenantId, UUID organizationId) {

        return agreementRepository
                .findByTenant_IdAndOrganization_Id(tenantId, organizationId)
                .stream()
                .map(agreementMapper::toResponse)
                .toList();
    }

    // =================================================
    // FLUJO DE ESTADO
    // =================================================

    @Override
    public AgreementResponse submitForSignature(UUID tenantId, UUID agreementId) {

        InternshipAgreement agreement = getAgreementOrThrow(tenantId, agreementId);

        requireStatus(agreement, AgreementStatus.DRAFT);

        agreement.setStatus(AgreementStatus.PENDING_SIGNATURE);

        return agreementMapper.toResponse(agreement);
    }

    @Override
    public AgreementResponse activate(UUID tenantId, UUID agreementId) {

        InternshipAgreement agreement = getAgreementOrThrow(tenantId, agreementId);

        requireStatus(agreement, AgreementStatus.PENDING_SIGNATURE);

        agreement.setStatus(AgreementStatus.ACTIVE);

        return agreementMapper.toResponse(agreement);
    }

    @Override
    public AgreementResponse suspend(UUID tenantId, UUID agreementId, String reason) {

        InternshipAgreement agreement = getAgreementOrThrow(tenantId, agreementId);

        requireStatus(agreement, AgreementStatus.ACTIVE);

        agreement.setStatus(AgreementStatus.SUSPENDED);

        return agreementMapper.toResponse(agreement);
    }

    @Override
    public AgreementResponse reactivate(UUID tenantId, UUID agreementId) {

        InternshipAgreement agreement = getAgreementOrThrow(tenantId, agreementId);

        requireStatus(agreement, AgreementStatus.SUSPENDED);

        agreement.setStatus(AgreementStatus.ACTIVE);

        return agreementMapper.toResponse(agreement);
    }

    @Override
    public AgreementResponse close(UUID tenantId, UUID agreementId) {

        InternshipAgreement agreement = getAgreementOrThrow(tenantId, agreementId);

        requireStatus(agreement, AgreementStatus.ACTIVE);

        agreement.setStatus(AgreementStatus.CLOSED);

        return agreementMapper.toResponse(agreement);
    }

    @Override
    public AgreementResponse cancel(UUID tenantId, UUID agreementId, String reason) {

        InternshipAgreement agreement = getAgreementOrThrow(tenantId, agreementId);

        if (agreement.getStatus() == AgreementStatus.ACTIVE) {
            throw new IllegalStateException("Cannot cancel an ACTIVE agreement");
        }

        agreement.setStatus(AgreementStatus.CANCELLED);

        return agreementMapper.toResponse(agreement);
    }

    // =================================================
    // MÉTODOS PRIVADOS
    // =================================================

    private InternshipAgreement getAgreementOrThrow(UUID tenantId, UUID agreementId) {

        return agreementRepository
                .findByIdAndTenant_Id(agreementId, tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Agreement not found"));
    }

    private void requireStatus(InternshipAgreement agreement, AgreementStatus expected) {

        if (agreement.getStatus() != expected) {
            throw new IllegalStateException(
                    "Invalid state transition. Expected: "
                            + expected
                            + ", but was: "
                            + agreement.getStatus());
        }
    }
}