package com.klastr.klastrbackend.dto.internship;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class CreateStudentInternshipRequest {

    @NotNull
    private UUID studentId;

    @NotNull
    private UUID organizationId;

    @NotNull
    private UUID agreementId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private Integer requiredHours;

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(UUID agreementId) {
        this.agreementId = agreementId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getRequiredHours() {
        return requiredHours;
    }

    public void setRequiredHours(Integer requiredHours) {
        this.requiredHours = requiredHours;
    }
}