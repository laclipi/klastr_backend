package com.klastr.klastrbackend.domain.internship.attendance;

import java.time.LocalDate;
import java.util.UUID;

import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipAttendanceWeek {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    private StudentInternship internship;

    @Column(nullable = false)
    private LocalDate weekStart;

    @Column(nullable = false)
    private LocalDate weekEnd;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WeekStatus status = WeekStatus.OPEN;

    // -------------------------------------------------
    // STATE MACHINE
    // -------------------------------------------------

    public void submit() {
        requireStatus(WeekStatus.OPEN);
        this.status = WeekStatus.SUBMITTED;
    }

    public void approve(String comment) {
        requireStatus(WeekStatus.SUBMITTED);
        this.status = WeekStatus.APPROVED;
    }

    public void reject(String comment) {
        requireStatus(WeekStatus.SUBMITTED);
        this.status = WeekStatus.REJECTED;
    }

    private void requireStatus(WeekStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException(
                    "Invalid state transition. Expected: "
                            + expected + ", but was: " + this.status);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = WeekStatus.OPEN;
        }
    }
}