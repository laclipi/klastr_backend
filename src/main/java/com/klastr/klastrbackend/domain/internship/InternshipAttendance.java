package com.klastr.klastrbackend.domain.internship;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.klastr.klastrbackend.domain.user.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "internship_attendance", uniqueConstraints = {
        @UniqueConstraint(name = "uk_attendance_internship_date", columnNames = { "internship_id", "date" })
}, indexes = {
        @Index(name = "idx_attendance_internship", columnList = "internship_id"),
        @Index(name = "idx_attendance_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipAttendance {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    // -------------------------------------------------
    // RELACIÓN PRINCIPAL
    // -------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "internship_id", nullable = false, updatable = false)
    private StudentInternship internship;

    // -------------------------------------------------
    // RELACIÓN CON SEMANA
    // -------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_id", nullable = false)
    private InternshipAttendanceWeek week;

    // -------------------------------------------------
    // REGISTRO DIARIO
    // -------------------------------------------------
    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double hoursWorked;

    @Column(length = 1000)
    private String notes;

    // -------------------------------------------------
    // VALIDACIÓN
    // -------------------------------------------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private User validatedBy;

    private LocalDateTime validatedAt;

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
            this.status = AttendanceStatus.PENDING;
        }

        validateHours();
    }

    @PreUpdate
    protected void onUpdate() {
        validateHours();
    }

    private void validateHours() {
        if (hoursWorked == null || hoursWorked <= 0) {
            throw new IllegalStateException("Hours worked must be greater than zero");
        }

        if (hoursWorked > 24) {
            throw new IllegalStateException("Hours worked cannot exceed 24 per day");
        }
    }
}