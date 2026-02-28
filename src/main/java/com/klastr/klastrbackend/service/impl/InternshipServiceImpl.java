package com.klastr.klastrbackend.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klastr.klastrbackend.domain.internship.attendance.AttendanceStatus;
import com.klastr.klastrbackend.domain.internship.attendance.InternshipAttendance;
import com.klastr.klastrbackend.domain.internship.attendance.InternshipAttendanceWeek;
import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;
import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.domain.tenant.Tenant;
import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;
import com.klastr.klastrbackend.dto.internship.RegisterAttendanceRequest;
import com.klastr.klastrbackend.exception.BusinessException;
import com.klastr.klastrbackend.exception.EntityNotFoundException;
import com.klastr.klastrbackend.repository.InternshipAttendanceRepository;
import com.klastr.klastrbackend.repository.InternshipAttendanceWeekRepository;
import com.klastr.klastrbackend.repository.OrganizationRepository;
import com.klastr.klastrbackend.repository.StudentInternshipRepository;
import com.klastr.klastrbackend.repository.StudentRepository;
import com.klastr.klastrbackend.repository.TenantRepository;
import com.klastr.klastrbackend.service.InternshipService;

import lombok.RequiredArgsConstructor;

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
                .orElseThrow(() -> new EntityNotFoundException("Tenant not found"));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));

        if (!organization.getTenant().getId().equals(tenantId)) {
            throw new BusinessException(
                    "Organization does not belong to tenant",
                    HttpStatus.BAD_REQUEST);
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

    @Override
    @Transactional(readOnly = true)
    public List<InternshipResponse> findByStudent(UUID tenantId, UUID studentId) {
        return internshipRepository
                .findByTenant_IdAndStudent_Id(tenantId, studentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InternshipResponse findById(UUID tenantId, UUID internshipId) {
        return mapToResponse(getInternshipOrThrow(internshipId, tenantId));
    }

    // -------------------------------------------------
    // COMPLETE
    // -------------------------------------------------

    @Override
    public InternshipResponse complete(UUID tenantId, UUID internshipId) {

        StudentInternship internship = getInternshipOrThrow(internshipId, tenantId);

        double approvedHours = Optional
                .ofNullable(attendanceRepository
                        .sumApprovedHours(internshipId, AttendanceStatus.APPROVED))
                .orElse(0.0);

        if (Double.compare(approvedHours, internship.getRequiredHours()) < 0) {
            throw new BusinessException(
                    "Cannot complete internship. Approved hours: "
                            + approvedHours + " / Required: "
                            + internship.getRequiredHours(),
                    HttpStatus.BAD_REQUEST);
        }

        internship.complete();
        return mapToResponse(internshipRepository.save(internship));
    }

    // -------------------------------------------------
    // LIFECYCLE
    // -------------------------------------------------

    @Override
    public InternshipResponse activate(UUID tenantId, UUID internshipId) {
        StudentInternship internship = getInternshipOrThrow(internshipId, tenantId);
        internship.activate();
        return mapToResponse(internshipRepository.save(internship));
    }

    @Override
    public InternshipResponse approve(UUID tenantId, UUID internshipId) {
        StudentInternship internship = getInternshipOrThrow(internshipId, tenantId);
        internship.approve();
        return mapToResponse(internshipRepository.save(internship));
    }

    @Override
    public InternshipResponse reject(UUID tenantId, UUID internshipId, String reason) {
        StudentInternship internship = getInternshipOrThrow(internshipId, tenantId);
        internship.reject();
        return mapToResponse(internshipRepository.save(internship));
    }

    @Override
    public InternshipResponse cancel(UUID tenantId, UUID internshipId, String reason) {
        StudentInternship internship = getInternshipOrThrow(internshipId, tenantId);
        internship.cancel();
        return mapToResponse(internshipRepository.save(internship));
    }

    // -------------------------------------------------
    // ATTENDANCE
    // -------------------------------------------------

    @Override
    public void registerAttendance(UUID tenantId, UUID internshipId, RegisterAttendanceRequest request) {

        StudentInternship internship = getInternshipOrThrow(internshipId, tenantId);

        if (!internship.getStatus().isActive()) {
            throw new BusinessException(
                    "Attendance can only be registered for ACTIVE internships",
                    HttpStatus.BAD_REQUEST);
        }

        if (request.getDate().isBefore(internship.getStartDate())
                || request.getDate().isAfter(internship.getEndDate())) {
            throw new BusinessException(
                    "Date outside internship range",
                    HttpStatus.BAD_REQUEST);
        }

        if (attendanceRepository.existsByInternship_IdAndDate(internshipId, request.getDate())) {
            throw new BusinessException(
                    "Attendance already registered for this date",
                    HttpStatus.BAD_REQUEST);
        }

        InternshipAttendanceWeek week = weekRepository
                .findByInternship_IdAndWeekStartLessThanEqualAndWeekEndGreaterThanEqual(
                        internshipId,
                        request.getDate(),
                        request.getDate())
                .orElseGet(() -> createWeek(internship, request.getDate()));

        InternshipAttendance attendance = InternshipAttendance.builder()
                .internship(internship)
                .week(week)
                .date(request.getDate())
                .hoursWorked(request.getHours().doubleValue())
                .status(AttendanceStatus.PENDING)
                .build();

        attendanceRepository.save(attendance);
    }

    @Override
    public void submitWeek(UUID tenantId, UUID internshipId, UUID weekId) {

        getInternshipOrThrow(internshipId, tenantId);

        InternshipAttendanceWeek week = weekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Week not found"));

        if (!week.getInternship().getId().equals(internshipId)) {
            throw new BusinessException("Week does not belong to internship", HttpStatus.BAD_REQUEST);
        }

        week.submit();
        weekRepository.save(week);
    }

    @Override
    public void rejectWeek(UUID tenantId, UUID internshipId, UUID weekId, String reason) {

        getInternshipOrThrow(internshipId, tenantId);

        InternshipAttendanceWeek week = weekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Week not found"));

        if (!week.getInternship().getId().equals(internshipId)) {
            throw new BusinessException("Week does not belong to internship", HttpStatus.BAD_REQUEST);
        }

        week.reject(null);
    }

    @Override
    public void approveWeek(UUID tenantId, UUID internshipId, UUID weekId) {

        getInternshipOrThrow(internshipId, tenantId);

        InternshipAttendanceWeek week = weekRepository.findById(weekId)
                .orElseThrow(() -> new EntityNotFoundException("Week not found"));

        if (!week.getInternship().getId().equals(internshipId)) {
            throw new BusinessException("Week does not belong to internship", HttpStatus.BAD_REQUEST);
        }

        week.approve(null);

        attendanceRepository
                .findAllByWeek_Id(weekId)
                .forEach(att -> {
                    att.setStatus(AttendanceStatus.APPROVED);
                    attendanceRepository.save(att);
                });

        weekRepository.save(week);
    }

    // -------------------------------------------------
    // CALCULATIONS
    // -------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Double calculateApprovedHours(UUID tenantId, UUID internshipId) {

        getInternshipOrThrow(internshipId, tenantId);

        return Optional
                .ofNullable(attendanceRepository
                        .sumApprovedHours(internshipId, AttendanceStatus.APPROVED))
                .orElse(0.0);
    }

    // -------------------------------------------------
    // HELPERS
    // -------------------------------------------------

    private StudentInternship getInternshipOrThrow(UUID internshipId, UUID tenantId) {

        StudentInternship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new EntityNotFoundException("Internship not found"));

        if (!internship.getTenant().getId().equals(tenantId)) {
            throw new BusinessException("Internship does not belong to tenant", HttpStatus.BAD_REQUEST);
        }

        return internship;
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