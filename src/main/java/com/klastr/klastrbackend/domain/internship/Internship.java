package com.klastr.klastrbackend.domain.internship;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.tenant.BaseTenantEntity;
import com.klastr.klastrbackend.domain.user.User;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(
        name = "internships",
        indexes = {
            @Index(name = "idx_internship_student", columnList = "student_id"),
            @Index(name = "idx_internship_organization", columnList = "organization_id"),
            @Index(name = "idx_internship_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Internship extends BaseTenantEntity {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    // -------------------------------------------------
    // RELACIONES PRINCIPALES
    // -------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, updatable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false, updatable = false)
    private Organization organization;

    // -------------------------------------------------
    // INFORMACIÓN ACADÉMICA
    // -------------------------------------------------
    @Column(nullable = false)
    private Integer academicYear; // Ej: 2025

    @Column(nullable = false, length = 20)
    private String academicPeriod; // Ej: "2024-2025"

    @Column(nullable = false)
    private Integer requiredHours; // Ej: 400

    // -------------------------------------------------
    // FECHAS
    // -------------------------------------------------
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    // -------------------------------------------------
    // ESTADO DEL FLUJO
    // -------------------------------------------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private InternshipStatus status;

    // -------------------------------------------------
    // METADATA
    // -------------------------------------------------
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // -------------------------------------------------
    // LIFECYCLE
    // -------------------------------------------------
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = InternshipStatus.PENDING;
        }

        validateDates();
    }

    @PreUpdate
    protected void onUpdate() {
        validateDates();
    }

    private void validateDates() {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalStateException("End date cannot be before start date");
        }
    }
}
