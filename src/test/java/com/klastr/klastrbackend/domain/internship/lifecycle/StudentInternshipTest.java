package com.klastr.klastrbackend.domain.internship.lifecycle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.klastr.klastrbackend.exception.BusinessException;

class StudentInternshipTest {

    @Test
    void should_start_in_draft_status() {

        StudentInternship internship = StudentInternship.builder()
                .requiredHours(300)
                .build();

        internship.onCreate(); // simula @PrePersist

        assertEquals(StudentInternshipStatus.DRAFT, internship.getStatus());
    }

    @Test
    void should_approve_from_draft() {

        StudentInternship internship = StudentInternship.builder()
                .requiredHours(300)
                .build();

        internship.onCreate();
        internship.approve();

        assertEquals(StudentInternshipStatus.APPROVED, internship.getStatus());
    }

    @Test
    void should_not_activate_if_not_approved() {

        StudentInternship internship = StudentInternship.builder()
                .requiredHours(300)
                .build();

        internship.onCreate();

        assertThrows(BusinessException.class, internship::activate);
    }

    @Test
    void should_activate_if_approved() {

        StudentInternship internship = StudentInternship.builder()
                .requiredHours(300)
                .build();

        internship.onCreate();
        internship.approve();
        internship.activate();

        assertEquals(StudentInternshipStatus.ACTIVE, internship.getStatus());
    }
}