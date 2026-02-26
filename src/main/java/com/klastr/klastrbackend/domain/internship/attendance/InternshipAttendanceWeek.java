package com.klastr.klastrbackend.domain.internship.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;
import com.klastr.klastrbackend.domain.user.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "internship_attendance_weeks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipAttendanceWeek {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "internship_id", nullable = false, updatable = false)
    private StudentInternship internship;

    @Column(nullable = false)
    private LocalDate weekStart;

    @Column(nullable = false)
    private LocalDate weekEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WeekStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private User validatedBy;

    private LocalDateTime validatedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ==========================================
    // LIFECYCLE
    // ==========================================

    public void submit() {
        requireStatus(WeekStatus.OPEN);
        this.status = WeekStatus.SUBMITTED;
    }

    public void approve(User validator) {
        requireStatus(WeekStatus.SUBMITTED);
        this.status = WeekStatus.APPROVED;
        this.validatedBy = validator;
        this.validatedAt = LocalDateTime.now();
    }

    public void reject(User validator) {
        requireStatus(WeekStatus.SUBMITTED);
        this.status = WeekStatus.REJECTED;
        this.validatedBy = validator;
        this.validatedAt = LocalDateTime.now();
    }

    private void requireStatus(WeekStatus expected) {
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
            this.status = WeekStatus.OPEN;
        }
    }
}