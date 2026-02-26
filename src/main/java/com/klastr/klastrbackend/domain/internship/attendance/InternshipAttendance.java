package com.klastr.klastrbackend.domain.internship.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;
import com.klastr.klastrbackend.domain.user.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "internship_attendances", uniqueConstraints = @UniqueConstraint(columnNames = { "internship_id",
        "date" }))
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "internship_id", nullable = false)
    private StudentInternship internship;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "week_id", nullable = false)
    private InternshipAttendanceWeek week;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double hoursWorked;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private User validatedBy;

    private LocalDateTime validatedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ==========================================
    // LIFECYCLE
    // ==========================================

    public void approve(User validator) {
        requireStatus(AttendanceStatus.PENDING);
        this.status = AttendanceStatus.APPROVED;
        this.validatedBy = validator;
        this.validatedAt = LocalDateTime.now();
    }

    public void reject(User validator) {
        requireStatus(AttendanceStatus.PENDING);
        this.status = AttendanceStatus.REJECTED;
        this.validatedBy = validator;
        this.validatedAt = LocalDateTime.now();
    }

    private void requireStatus(AttendanceStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException(
                    "Invalid state transition. Expected: "
                            + expected
                            + ", but was: "
                            + this.status);
        }
    }

    // ==========================================
    // PERSISTENCE
    // ==========================================

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = AttendanceStatus.PENDING;
        }

        if (this.hoursWorked == null || this.hoursWorked <= 0) {
            throw new IllegalStateException("Hours worked must be greater than 0");
        }
    }
}