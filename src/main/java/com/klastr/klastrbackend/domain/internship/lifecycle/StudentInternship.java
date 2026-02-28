package com.klastr.klastrbackend.domain.internship.lifecycle;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.klastr.klastrbackend.domain.base.BaseEntity;
import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.domain.tenant.Tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "internships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentInternship extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private Integer academicYear;

    @Column(nullable = false, length = 20)
    private String academicPeriod;

    @Column(nullable = false)
    private Integer requiredHours;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StudentInternshipStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ==========================================
    // LIFECYCLE MANAGEMENT
    // ==========================================

    public void approve() {
        requireStatus(StudentInternshipStatus.DRAFT);
        this.status = StudentInternshipStatus.APPROVED;
    }

    public void reject() {
        requireStatus(StudentInternshipStatus.DRAFT);
        this.status = StudentInternshipStatus.REJECTED;
    }

    public void activate() {
        requireStatus(StudentInternshipStatus.APPROVED);
        this.status = StudentInternshipStatus.ACTIVE;
    }

    public void complete() {
        requireStatus(StudentInternshipStatus.ACTIVE);
        this.status = StudentInternshipStatus.COMPLETED;
    }

    public void cancel() {
        if (this.status == StudentInternshipStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Completed internships cannot be cancelled");
        }
        this.status = StudentInternshipStatus.CANCELLED;
    }

    // ✅ Helper para tests y lógica de dominio
    public boolean isCompleted() {
        return this.status == StudentInternshipStatus.COMPLETED;
    }

    private void requireStatus(StudentInternshipStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException(
                    "Invalid state transition. Expected: "
                            + expected
                            + ", but was: "
                            + this.status);
        }
    }

    // ==========================================
    // PERSISTENCE HOOK
    // ==========================================

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = StudentInternshipStatus.DRAFT;
        }
    }
}