package com.klastr.klastrbackend.domain.internship.attendance;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InternshipAttendanceWeekTest {

    private InternshipAttendanceWeek newWeek() {
        return InternshipAttendanceWeek.builder()
                .internship(null) // si es obligatorio, mock o crea dummy
                .weekStart(LocalDate.now())
                .weekEnd(LocalDate.now().plusDays(6))
                .status(WeekStatus.OPEN)
                .build();
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

        assertThrows(IllegalStateException.class,
                () -> week.changeWeekDates(
                        LocalDate.now(),
                        LocalDate.now().plusDays(6)));
    }
}