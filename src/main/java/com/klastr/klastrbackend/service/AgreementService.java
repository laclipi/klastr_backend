package com.klastr.klastrbackend.service;

import java.util.List;
import java.util.UUID;

import com.klastr.klastrbackend.dto.internship.CreateAgreementRequest;
import com.klastr.klastrbackend.dto.internship.AgreementResponse;

public interface AgreementService {

        AgreementResponse create(
                        UUID tenantId,
                        CreateAgreementRequest request);

        AgreementResponse findById(
                        UUID tenantId,
                        UUID agreementId);

        List<AgreementResponse> findByOrganization(
                        UUID tenantId,
                        UUID organizationId);

        AgreementResponse submitForSignature(
                        UUID tenantId,
                        UUID agreementId);

        AgreementResponse activate(
                        UUID tenantId,
                        UUID agreementId);

        AgreementResponse suspend(
                        UUID tenantId,
                        UUID agreementId,
                        String reason);

        AgreementResponse reactivate(
                        UUID tenantId,
                        UUID agreementId);

        AgreementResponse close(
                        UUID tenantId,
                        UUID agreementId);

        AgreementResponse cancel(
                        UUID tenantId,
                        UUID agreementId,
                        String reason);
}