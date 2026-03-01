package com.klastr.klastrbackend.domain.internship.attendance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeekStatusTest {

    @Test
    void open_can_submit() {
        WeekStatus result = WeekStatus.OPEN.submit();
        assertEquals(WeekStatus.SUBMITTED, result);
    }

    @Test
    void submitted_can_approve() {
        WeekStatus result = WeekStatus.SUBMITTED.approve();
        assertEquals(WeekStatus.APPROVED, result);
    }

    @Test
    void submitted_can_reject() {
        WeekStatus result = WeekStatus.SUBMITTED.reject();
        assertEquals(WeekStatus.REJECTED, result);
    }

    @Test
    void open_cannot_approve() {
        assertThrows(IllegalStateException.class,
                () -> WeekStatus.OPEN.approve());
    }

    @Test
    void approved_cannot_submit() {
        assertThrows(IllegalStateException.class,
                () -> WeekStatus.APPROVED.submit());
    }

    @Test
    void open_is_editable() {
        assertTrue(WeekStatus.OPEN.isEditable());
    }

    @Test
    void submitted_is_not_editable() {
        assertFalse(WeekStatus.SUBMITTED.isEditable());
    }

    @Test
    void rejected_is_editable() {
        assertTrue(WeekStatus.REJECTED.isEditable());
    }
}