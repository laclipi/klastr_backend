package com.klastr.klastrbackend.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.domain.internship.attendance.AttendanceStatus;
import com.klastr.klastrbackend.domain.internship.attendance.InternshipAttendance;
import com.klastr.klastrbackend.domain.internship.attendance.InternshipAttendanceWeek;
import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;
import com.klastr.klastrbackend.domain.tenant.Tenant;
import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;
import com.klastr.klastrbackend.dto.internship.RegisterAttendanceRequest;
import com.klastr.klastrbackend.repository.StudentInternshipRepository;
import com.klastr.klastrbackend.repository.InternshipAttendanceRepository;
import com.klastr.klastrbackend.repository.InternshipAttendanceWeekRepository;
import com.klastr.klastrbackend.repository.OrganizationRepository;
import com.klastr.klastrbackend.repository.StudentRepository;
import com.klastr.klastrbackend.repository.TenantRepository;
import com.klastr.klastrbackend.service.InternshipService;

@Service
@RequiredArgsConstructor
@Transactional
public class InternshipServiceImpl implements InternshipService {

    private final StudentInternshipRepository internshipRepository;
    private final StudentRepository studentRepository;
    private final OrganizationRepository organizationRepository;
    private final TenantRepository tenantRepository;
    private final InternshipAttendanceRepository attendanceRepository;
    private final InternshipAttendanceWeekRepository weekRepository;

    // -------------------------------------------------
    // CREATE
    // -------------------------------------------------

    @Override
    public InternshipResponse create(UUID tenantId, CreateInternshipRequest request) {

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        if (!organization.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Organization does not belong to tenant");
        }

        StudentInternship internship = StudentInternship.builder()
                .tenant(tenant)
                .student(student)
                .organization(organization)
                .academicYear(request.getAcademicYear())
                .academicPeriod(request.getAcademicPeriod())
                .requiredHours(request.getRequiredHours())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        return mapToResponse(internshipRepository.save(internship));
    }

    // -------------------------------------------------
    // COMPLETE WITH HOURS VALIDATION
    // -------------------------------------------------

    @Override
    public InternshipResponse complete(UUID tenantId, UUID internshipId) {

        StudentInternship internship = getInternshipOrThrow(internshipId, tenantId);

        Double approvedHours = attendanceRepository
                .sumApprovedHours(internshipId, AttendanceStatus.APPROVED);

        if (approvedHours == null) {
            approvedHours = 0.0;
        }

        if (approvedHours < internship.getRequiredHours()) {
            throw new IllegalStateException(
                    "Cannot complete internship. Approved hours: "
                            + approvedHours
                            + " / Required: "
                            + internship.getRequiredHours());
        }

        internship.complete();

        return mapToResponse(internshipRepository.save(internship));
    }

    // -------------------------------------------------
    // ATTENDANCE
    // -------------------------------------------------

    @Override
    public void registerAttendance(UUID tenantId, UUID internshipId, RegisterAttendanceRequest request) {

        StudentInternship internship = getInternshipOrThrow(internshipId, tenantId);

        if (!internship.getStatus().isActive()) {
            throw new IllegalStateException("Attendance can only be registered for ACTIVE internships");
        }

        if (request.getDate().isBefore(internship.getStartDate())
                || request.getDate().isAfter(internship.getEndDate())) {
            throw new IllegalStateException("Date outside internship range");
        }

        if (attendanceRepository.existsByInternship_IdAndDate(internshipId, request.getDate())) {
            throw new IllegalStateException("Attendance already registered for this date");
        }

        InternshipAttendanceWeek week = weekRepository
                .findByInternship_IdAndWeekStartLessThanEqualAndWeekEndGreaterThanEqual(
                        internshipId,
                        request.getDate(),
                        request.getDate())
                .orElseGet(() -> createWeek(internship, request.getDate()));

        if (!week.getStatus().isOpen()) {
            throw new IllegalStateException("Cannot register attendance in a closed week");
        }

        InternshipAttendance attendance = InternshipAttendance.builder()
                .internship(internship)
                .week(week)
                .date(request.getDate())
                .hoursWorked(request.getHoursWorked())
                .status(AttendanceStatus.PENDING)
                .build();

        attendanceRepository.save(attendance);
    }

    // -------------------------------------------------
    // HELPERS
    // -------------------------------------------------

    private StudentInternship getInternshipOrThrow(UUID internshipId, UUID tenantId) {

        StudentInternship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        validateTenant(internship, tenantId);

        return internship;
    }

    private void validateTenant(StudentInternship internship, UUID tenantId) {

        if (!internship.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Internship does not belong to tenant");
        }
    }

    private InternshipAttendanceWeek createWeek(StudentInternship internship, LocalDate date) {

        LocalDate weekStart = date.minusDays(date.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);

        InternshipAttendanceWeek week = InternshipAttendanceWeek.builder()
                .internship(internship)
                .weekStart(weekStart)
                .weekEnd(weekEnd)
                .build();

        return weekRepository.save(week);
    }

    private InternshipResponse mapToResponse(StudentInternship internship) {

        return InternshipResponse.builder()
                .id(internship.getId())
                .studentId(internship.getStudent().getId())
                .organizationId(internship.getOrganization().getId())
                .academicYear(internship.getAcademicYear())
                .academicPeriod(internship.getAcademicPeriod())
                .requiredHours(internship.getRequiredHours())
                .startDate(internship.getStartDate())
                .endDate(internship.getEndDate())
                .status(internship.getStatus().name())
                .build();
    }
}