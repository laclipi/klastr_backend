package com.klastr.klastrbackend.dto.internship;

import java.time.LocalDate;
import java.util.UUID;

import com.klastr.klastrbackend.domain.internship.StudentInternshipStatus;

public class StudentInternshipResponse {

    private UUID id;
    private UUID studentId;
    private UUID organizationId;
    private UUID agreementId;

    private LocalDate startDate;
    private LocalDate endDate;

    private Integer requiredHours;
    private Integer approvedHours;

    private StudentInternshipStatus status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public Integer getApprovedHours() {
        return approvedHours;
    }

    public void setApprovedHours(Integer approvedHours) {
        this.approvedHours = approvedHours;
    }

    public StudentInternshipStatus getStatus() {
        return status;
    }

    public void setStatus(StudentInternshipStatus status) {
        this.status = status;
    }
}