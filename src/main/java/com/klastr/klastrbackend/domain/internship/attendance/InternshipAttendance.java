package com.klastr.klastrbackend.domain.internship.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.klastr.klastrbackend.domain.base.BaseEntity;
import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "internship_attendance", uniqueConstraints = {
        @UniqueConstraint(name = "uk_internship_date", columnNames = { "internship_id", "date" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipAttendance extends BaseEntity {

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
    @Column(nullable = false, length = 30)
    private AttendanceStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {

        if (hoursWorked == null || hoursWorked <= 0) {
            throw new IllegalStateException("Hours worked must be greater than 0");
        }

        if (hoursWorked > 8) {
            throw new IllegalStateException("Hours worked cannot exceed 8 hours per day");
        }

        if (status == null) {
            status = AttendanceStatus.PENDING;
        }

        createdAt = LocalDateTime.now();
    }
}