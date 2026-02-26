package com.klastr.klastrbackend.domain.internship;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.klastr.klastrbackend.domain.base.BaseEntity;
import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.domain.internship.StudentInternshipStatus;
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
@Table(name = "internships") // Se mantiene el nombre actual para no romper el esquema existente
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentInternship extends BaseEntity {

    /**
     * Alumno que realiza la FCT.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /**
     * Empresa donde el alumno realiza la FCT.
     * En Fase 2 se vinculará mediante InternshipAgreement.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization company;

    /**
     * Tenant al que pertenece la FCT.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private Integer academicYear;

    @Column(nullable = false, length = 20)
    private String academicPeriod;

    /**
     * Total de horas obligatorias que debe completar el alumno.
     */
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

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = StudentInternshipStatus.DRAFT;
        }
    }
}