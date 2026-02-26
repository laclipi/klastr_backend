package com.klastr.klastrbackend.dto.internship;

import java.time.LocalDate;
import java.util.UUID;

import com.klastr.klastrbackend.domain.internship.agreement.AgreementStatus;

public class AgreementResponse {

    private UUID id;
    private UUID organizationId;
    private String agreementNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private AgreementStatus status;

    public AgreementResponse(
            UUID id,
            UUID organizationId,
            String agreementNumber,
            LocalDate startDate,
            LocalDate endDate,
            String description,
            AgreementStatus status) {

        this.id = id;
        this.organizationId = organizationId;
        this.agreementNumber = agreementNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getAgreementNumber() {
        return agreementNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }

    public AgreementStatus getStatus() {
        return status;
    }
}