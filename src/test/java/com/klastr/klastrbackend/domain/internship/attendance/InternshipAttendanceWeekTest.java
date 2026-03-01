package com.klastr.klastrbackend.domain.internship.attendance;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;

class InternshipAttendanceWeekTest {

    private InternshipAttendanceWeek newWeek() {

        StudentInternship dummyInternship = StudentInternship.builder().build(); // mínimo válido

        LocalDate monday = LocalDate.of(2026, 3, 2); // lunes real

        return InternshipAttendanceWeek.create(dummyInternship, monday);
    }

    @Test
    void submit_changes_status() {
        InternshipAttendanceWeek week = newWeek();

        week.submit();

        assertEquals(WeekStatus.SUBMITTED, week.getStatus());
    }

    @Test
    void approve_from_submitted_changes_status() {
        InternshipAttendanceWeek week = newWeek();
        week.submit();

        week.approve("ok");

        assertEquals(WeekStatus.APPROVED, week.getStatus());
    }

    @Test
    void cannot_approve_from_open() {
        InternshipAttendanceWeek week = newWeek();

        assertThrows(IllegalStateException.class,
                () -> week.approve("nope"));
    }

    @Test
    void approved_week_is_not_editable() {
        InternshipAttendanceWeek week = newWeek();
        week.submit();
        week.approve("ok");

        LocalDate monday = LocalDate.of(2026, 3, 9);

        assertThrows(IllegalStateException.class,
                () -> week.changeWeekDates(monday));
    }
}