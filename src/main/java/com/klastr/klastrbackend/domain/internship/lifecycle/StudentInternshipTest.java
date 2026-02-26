package com.klastr.klastrbackend.domain.internship.lifecycle;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StudentInternshipTest {

    @Test
    void shouldApproveFromDraft() {

        StudentInternship internship = StudentInternship.builder()
                .status(StudentInternshipStatus.DRAFT)
                .requiredHours(100)
                .build();

        internship.approve();

        assertEquals(StudentInternshipStatus.APPROVED, internship.getStatus());
    }

    @Test
    void shouldNotActivateFromDraft() {

        StudentInternship internship = StudentInternship.builder()
                .status(StudentInternshipStatus.DRAFT)
                .requiredHours(100)
                .build();

        assertThrows(IllegalStateException.class, internship::activate);
    }

    @Test
    void shouldActivateFromApproved() {

        StudentInternship internship = StudentInternship.builder()
                .status(StudentInternshipStatus.APPROVED)
                .requiredHours(100)
                .build();

        internship.activate();

        assertEquals(StudentInternshipStatus.ACTIVE, internship.getStatus());
    }

    @Test
    void shouldNotCompleteIfNotActive() {

        StudentInternship internship = StudentInternship.builder()
                .status(StudentInternshipStatus.APPROVED)
                .requiredHours(100)
                .build();

        assertThrows(IllegalStateException.class, internship::complete);
    }
}