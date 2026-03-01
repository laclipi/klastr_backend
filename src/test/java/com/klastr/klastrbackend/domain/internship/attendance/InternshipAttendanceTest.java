package com.klastr.klastrbackend.domain.internship.attendance;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

class InternshipAttendanceTest {

    @Test
    void should_throw_if_hours_are_zero() {

        assertThrows(IllegalStateException.class, () -> {

            InternshipAttendance.builder()
                    .date(LocalDate.now())
                    .hoursWorked(0.0)
                    .build()
                    .onCreate(); // fuerza validación
        });
    }

    @Test
    void should_throw_if_hours_exceed_12() {

        assertThrows(IllegalStateException.class, () -> {

            InternshipAttendance.builder()
                    .date(LocalDate.now())
                    .hoursWorked(15.0)
                    .build()
                    .onCreate();
        });
    }

    @Test
    void should_accept_valid_hours() {

        InternshipAttendance attendance = InternshipAttendance.builder()
                .date(LocalDate.now())
                .hoursWorked(8.0)
                .build();

        assertDoesNotThrow(attendance::onCreate);
    }
}