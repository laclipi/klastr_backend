package com.klastr.klastrbackend.domain.internship;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.klastr.klastrbackend.domain.base.BaseEntity;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.tenant.Tenant;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "internships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Internship extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
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
    private InternshipStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = InternshipStatus.PENDING;
        }
    }
}