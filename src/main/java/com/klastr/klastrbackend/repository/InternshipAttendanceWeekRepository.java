package com.klastr.klastrbackend.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klastr.klastrbackend.com.klastr.klastrbackend.domain.internship.attendance.InternshipAttendanceWeek;

public interface InternshipAttendanceWeekRepository
                extends JpaRepository<InternshipAttendanceWeek, UUID> {

        /**
         * Finds the week that contains a specific date for a given internship.
         *
         * Logic:
         * weekStart <= date <= weekEnd
         */
        Optional<InternshipAttendanceWeek> findByInternship_IdAndWeekStartLessThanEqualAndWeekEndGreaterThanEqual(
                        UUID internshipId,
                        LocalDate date1,
                        LocalDate date2);
}