package com.klastr.klastrbackend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.klastr.klastrbackend.domain.internship.attendance.AttendanceStatus;
import com.klastr.klastrbackend.domain.internship.attendance.InternshipAttendance;

public interface InternshipAttendanceRepository
                extends JpaRepository<InternshipAttendance, UUID> {

        @Query("""
                        SELECT COALESCE(SUM(a.hoursWorked), 0)
                        FROM InternshipAttendance a
                        WHERE a.internship.id = :internshipId
                          AND a.status = :status
                        """)
        Double sumApprovedHours(
                        @Param("internshipId") UUID internshipId,
                        @Param("status") AttendanceStatus status);
}