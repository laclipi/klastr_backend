package com.klastr.klastrbackend.domain.internship.agreement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.tenant.BaseTenantEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "internship_agreements", indexes = {
        @Index(name = "idx_agreement_organization", columnList = "organization_id"),
        @Index(name = "idx_agreement_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipAgreement extends BaseTenantEntity {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false, unique = true)
    private String agreementNumber;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AgreementStatus status;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = AgreementStatus.DRAFT;
        }

        if (this.version == null) {
            this.version = 1;
        }

        validateDates();
    }

    @PreUpdate
    protected void onUpdate() {
        validateDates();
    }

    private void validateDates() {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalStateException(
                    "Agreement end date cannot be before start date");
        }
    }
}