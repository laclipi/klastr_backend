package com.klastr.klastrbackend.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klastr.klastrbackend.domain.*;
import com.klastr.klastrbackend.domain.internship.AttendanceStatus;
import com.klastr.klastrbackend.domain.internship.Internship;
import com.klastr.klastrbackend.domain.internship.InternshipAttendance;
import com.klastr.klastrbackend.domain.internship.InternshipAttendanceWeek;
import com.klastr.klastrbackend.domain.internship.InternshipStatus;
import com.klastr.klastrbackend.domain.internship.WeekStatus;
import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.user.User;
import com.klastr.klastrbackend.dto.attendance.RegisterAttendanceRequest;
import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;
import com.klastr.klastrbackend.exception.BusinessException;
import com.klastr.klastrbackend.exception.ResourceNotFoundException;
import com.klastr.klastrbackend.mapper.InternshipMapper;
import com.klastr.klastrbackend.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InternshipServiceImpl implements InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipAttendanceRepository attendanceRepository;
    private final InternshipAttendanceWeekRepository weekRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final InternshipMapper internshipMapper;

    // -------------------------------------------------
    // CREATE
    // -------------------------------------------------
    @Override
    public InternshipResponse create(UUID tenantId, CreateInternshipRequest request) {

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        Internship internship = internshipMapper.toEntity(request);
        internship.setTenant(student.getTenant());
        internship.setStudent(student);
        internship.setOrganization(organization);

        Internship saved = internshipRepository.save(internship);

        return internshipMapper.toResponse(saved);
    }

    // -------------------------------------------------
    // FIND
    // -------------------------------------------------
    @Override
    public InternshipResponse findById(UUID tenantId, UUID internshipId) {

        Internship internship = getInternshipOrThrow(tenantId, internshipId);
        return internshipMapper.toResponse(internship);
    }

    @Override
    public List<InternshipResponse> findByStudent(UUID tenantId, UUID studentId) {

        return internshipRepository.findByStudentId(studentId)
                .stream()
                .map(internshipMapper::toResponse)
                .toList();
    }

    // -------------------------------------------------
    // STATE FLOW
    // -------------------------------------------------
    @Override
    public InternshipResponse approve(UUID tenantId, UUID internshipId) {

        Internship internship = getInternshipOrThrow(tenantId, internshipId);

        if (internship.getStatus() != InternshipStatus.PENDING) {
            throw new BusinessException("Only PENDING internships can be approved",
                    HttpStatus.CONFLICT);
        }

        internship.setStatus(InternshipStatus.APPROVED);

        return internshipMapper.toResponse(internship);
    }

    @Override
    public InternshipResponse reject(UUID tenantId, UUID internshipId, String reason) {

        Internship internship = getInternshipOrThrow(tenantId, internshipId);

        if (internship.getStatus() != InternshipStatus.PENDING) {
            throw new BusinessException("Only PENDING internships can be rejected",
                    HttpStatus.CONFLICT);
        }

        internship.setStatus(InternshipStatus.REJECTED);

        return internshipMapper.toResponse(internship);
    }

    @Override
    public InternshipResponse activate(UUID tenantId, UUID internshipId) {

        Internship internship = getInternshipOrThrow(tenantId, internshipId);

        if (internship.getStatus() != InternshipStatus.APPROVED) {
            throw new BusinessException("Only APPROVED internships can be activated",
                    HttpStatus.CONFLICT);
        }

        internship.setStatus(InternshipStatus.ACTIVE);

        return internshipMapper.toResponse(internship);
    }

    @Override
    public InternshipResponse cancel(UUID tenantId, UUID internshipId, String reason) {

        Internship internship = getInternshipOrThrow(tenantId, internshipId);

        if (internship.getStatus() == InternshipStatus.COMPLETED) {
            throw new BusinessException("Cannot cancel completed internship",
                    HttpStatus.CONFLICT);
        }

        internship.setStatus(InternshipStatus.CANCELLED);

        return internshipMapper.toResponse(internship);
    }

    @Override
    public InternshipResponse complete(UUID tenantId, UUID internshipId) {

        Internship internship = getInternshipOrThrow(tenantId, internshipId);

        Double approvedHours = calculateApprovedHours(tenantId, internshipId);

        if (approvedHours < internship.getRequiredHours()) {
            throw new BusinessException("Not enough approved hours to complete",
                    HttpStatus.CONFLICT);
        }

        internship.setStatus(InternshipStatus.COMPLETED);

        return internshipMapper.toResponse(internship);
    }

    // -------------------------------------------------
    // ATTENDANCE
    // -------------------------------------------------
    @Override
    public void registerAttendance(UUID tenantId,
            UUID internshipId,
            RegisterAttendanceRequest request) {

        Internship internship = getInternshipOrThrow(tenantId, internshipId);

        if (internship.getStatus() != InternshipStatus.ACTIVE) {
            throw new BusinessException("Internship is not active",
                    HttpStatus.CONFLICT);
        }

        LocalDate date = request.getDate();

        if (date.isBefore(internship.getStartDate())
                || date.isAfter(internship.getEndDate())) {
            throw new BusinessException("Date outside internship range",
                    HttpStatus.BAD_REQUEST);
        }

        InternshipAttendanceWeek week = weekRepository
                .findWeekContainingDate(internshipId, date)
                .orElseGet(() -> createWeek(internship, date));

        if (week.getStatus() == WeekStatus.APPROVED
                || week.getStatus() == WeekStatus.SUBMITTED) {
            throw new BusinessException("Week is locked",
                    HttpStatus.CONFLICT);
        }

        InternshipAttendance attendance = InternshipAttendance.builder()
                .internship(internship)
                .week(week)
                .date(date)
                .hoursWorked(request.getHours())
                .status(AttendanceStatus.PENDING)
                .build();

        attendanceRepository.save(attendance);
    }

    @Override
    public void submitWeek(UUID tenantId, UUID internshipId, UUID weekId) {

        InternshipAttendanceWeek week = weekRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("Week not found"));

        if (week.getStatus() != WeekStatus.OPEN) {
            throw new BusinessException("Week cannot be submitted",
                    HttpStatus.CONFLICT);
        }

        week.setStatus(WeekStatus.SUBMITTED);
    }

    @Override
    public void approveWeek(UUID tenantId, UUID internshipId, UUID weekId) {

        InternshipAttendanceWeek week = weekRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("Week not found"));

        if (week.getStatus() != WeekStatus.SUBMITTED) {
            throw new BusinessException("Week not ready for approval",
                    HttpStatus.CONFLICT);
        }

        week.setStatus(WeekStatus.APPROVED);
        week.setValidatedAt(LocalDateTime.now());
    }

    @Override
    public void rejectWeek(UUID tenantId, UUID internshipId, UUID weekId, String reason) {

        InternshipAttendanceWeek week = weekRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("Week not found"));

        week.setStatus(WeekStatus.REJECTED);
    }

    @Override
    public Double calculateApprovedHours(UUID tenantId, UUID internshipId) {
        return attendanceRepository.sumApprovedHours(internshipId);
    }

    // -------------------------------------------------
    // PRIVATE HELPERS
    // -------------------------------------------------
    private Internship getInternshipOrThrow(UUID tenantId, UUID internshipId) {

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

        if (!internship.getTenant().getId().equals(tenantId)) {
            throw new ResourceNotFoundException("Internship does not belong to tenant");
        }

        return internship;
    }

    private InternshipAttendanceWeek createWeek(Internship internship,
            LocalDate date) {

        LocalDate monday = date.with(DayOfWeek.MONDAY);
        LocalDate sunday = date.with(DayOfWeek.SUNDAY);

        if (monday.isBefore(internship.getStartDate())) {
            monday = internship.getStartDate();
        }

        if (sunday.isAfter(internship.getEndDate())) {
            sunday = internship.getEndDate();
        }

        InternshipAttendanceWeek week = InternshipAttendanceWeek.builder()
                .internship(internship)
                .weekStart(monday)
                .weekEnd(sunday)
                .status(WeekStatus.OPEN)
                .build();

        return weekRepository.save(week);
    }
}
