package com.klastr.klastrbackend.domain.internship.attendance;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 🔒 importante
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

    // --------------------------------
    // FACTORY (ISO week creation)
    // --------------------------------

    public static InternshipAttendanceWeek create(
            StudentInternship internship,
            LocalDate weekStart) {

        if (internship == null) {
            throw new IllegalArgumentException("internship cannot be null");
        }

        validateWeekStart(weekStart);

        return InternshipAttendanceWeek.builder()
                .internship(internship)
                .weekStart(weekStart)
                .weekEnd(weekStart.plusDays(6))
                .status(WeekStatus.OPEN)
                .build();
    }

    private static void validateWeekStart(LocalDate weekStart) {

        if (weekStart == null) {
            throw new IllegalArgumentException("weekStart cannot be null");
        }

        if (weekStart.getDayOfWeek() != DayOfWeek.MONDAY) {
            throw new IllegalStateException(
                    "Week must start on Monday (ISO-8601)");
        }
    }

    // --------------------------------
    // STATE MACHINE (delegated)
    // --------------------------------

    public void submit() {
        this.status = this.status.submit();
    }

    public void approve(String comment) {
        this.status = this.status.approve();
    }

    public void reject(String comment) {
        this.status = this.status.reject();
    }

    // --------------------------------
    // Domain behavior
    // --------------------------------

    public void changeWeekDates(LocalDate newStart) {

        ensureEditable();
        validateWeekStart(newStart);

        this.weekStart = newStart;
        this.weekEnd = newStart.plusDays(6);
    }

    private void ensureEditable() {
        if (!status.isEditable()) {
            throw new IllegalStateException(
                    "Week cannot be modified in status: " + status);
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = WeekStatus.OPEN;
        }
    }
}