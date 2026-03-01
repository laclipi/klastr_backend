package com.klastr.klastrbackend.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klastr.klastrbackend.domain.internship.attendance.*;
import com.klastr.klastrbackend.domain.internship.lifecycle.*;
import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;
import com.klastr.klastrbackend.dto.internship.RegisterAttendanceRequest;
import com.klastr.klastrbackend.exception.BusinessException;
import com.klastr.klastrbackend.exception.ResourceNotFoundException;
import com.klastr.klastrbackend.mapper.InternshipMapper;
import com.klastr.klastrbackend.repository.*;
import com.klastr.klastrbackend.service.InternshipService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InternshipServiceImpl implements InternshipService {

        private final StudentInternshipRepository internshipRepository;
        private final StudentRepository studentRepository;
        private final OrganizationRepository organizationRepository;
        private final InternshipAttendanceRepository attendanceRepository;
        private final InternshipAttendanceWeekRepository weekRepository;
        private final InternshipMapper internshipMapper;

        // =====================================================
        // CREATE
        // =====================================================

        @Override
        public InternshipResponse create(UUID tenantId, CreateInternshipRequest request) {

                Student student = studentRepository
                                .findById(request.getStudentId())
                                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

                Organization organization = organizationRepository
                                .findById(request.getOrganizationId())
                                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

                StudentInternship internship = internshipMapper.toEntity(request, student, organization);

                // 🔐 Multi-tenant seguro
                internship.setTenant(student.getTenant());

                internship = internshipRepository.save(internship);

                return internshipMapper.toResponse(internship);
        }

        // =====================================================
        // FIND
        // =====================================================

        @Override
        public InternshipResponse findById(UUID tenantId, UUID internshipId) {

                StudentInternship internship = internshipRepository
                                .findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

                return internshipMapper.toResponse(internship);
        }

        @Override
        public List<InternshipResponse> findByStudent(UUID tenantId, UUID studentId) {

                return internshipRepository
                                .findByTenant_IdAndStudent_Id(tenantId, studentId)
                                .stream()
                                .map(internshipMapper::toResponse)
                                .toList();
        }

        // =====================================================
        // STATE FLOW
        // =====================================================

        @Override
        public InternshipResponse approve(UUID tenantId, UUID internshipId) {

                StudentInternship internship = internshipRepository
                                .findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

                internship.approve();

                return internshipMapper.toResponse(internship);
        }

        @Override
        public InternshipResponse reject(UUID tenantId, UUID internshipId, String reason) {

                StudentInternship internship = internshipRepository
                                .findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

                internship.reject();

                return internshipMapper.toResponse(internship);
        }

        @Override
        public InternshipResponse activate(UUID tenantId, UUID internshipId) {

                StudentInternship internship = internshipRepository
                                .findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

                internship.activate();

                return internshipMapper.toResponse(internship);
        }

        @Override
        public InternshipResponse cancel(UUID tenantId, UUID internshipId, String reason) {

                StudentInternship internship = internshipRepository
                                .findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

                internship.cancel();

                return internshipMapper.toResponse(internship);
        }

        @Override
        public InternshipResponse complete(UUID tenantId, UUID internshipId) {

                StudentInternship internship = internshipRepository
                                .findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

                internship.complete();

                return internshipMapper.toResponse(internship);
        }

        // =====================================================
        // ATTENDANCE
        // =====================================================

        @Override
        public void registerAttendance(UUID tenantId, UUID internshipId, RegisterAttendanceRequest request) {

                StudentInternship internship = internshipRepository
                                .findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

                LocalDate date = request.getDate();

                if (attendanceRepository.existsByInternship_IdAndDate(internshipId, date)) {
                        throw new BusinessException(
                                        "Attendance already registered for this date",
                                        HttpStatus.CONFLICT);
                }

                LocalDate weekStart = date.with(DayOfWeek.MONDAY);
                LocalDate weekEnd = weekStart.plusDays(6);

                InternshipAttendanceWeek week = weekRepository
                                .findByInternship_IdAndWeekStartLessThanEqualAndWeekEndGreaterThanEqual(
                                                internshipId, date, date)
                                .orElseGet(() -> weekRepository.save(
                                                InternshipAttendanceWeek.builder()
                                                                .internship(internship)
                                                                .weekStart(weekStart)
                                                                .weekEnd(weekEnd)
                                                                .build()));

                InternshipAttendance attendance = InternshipAttendance.builder()
                                .internship(internship)
                                .week(week)
                                .date(date)
                                .hoursWorked(request.getHours().doubleValue())
                                .status(AttendanceStatus.PENDING)
                                .build();

                attendanceRepository.save(attendance);
        }

        @Override
        public void submitWeek(UUID tenantId, UUID internshipId, UUID weekId) {

                InternshipAttendanceWeek week = weekRepository
                                .findById(weekId)
                                .orElseThrow(() -> new ResourceNotFoundException("Week not found"));

                week.submit();
        }

        @Override
        public void approveWeek(UUID tenantId, UUID internshipId, UUID weekId) {

                InternshipAttendanceWeek week = weekRepository
                                .findById(weekId)
                                .orElseThrow(() -> new ResourceNotFoundException("Week not found"));

                internshipRepository
                                .findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

                week.approve("Approved");

                attendanceRepository
                                .findAllByWeek_IdAndWeek_Internship_Tenant_Id(weekId, tenantId)
                                .forEach(a -> a.setStatus(AttendanceStatus.APPROVED));
        }

        @Override
        public void rejectWeek(UUID tenantId, UUID internshipId, UUID weekId, String reason) {

                InternshipAttendanceWeek week = weekRepository
                                .findById(weekId)
                                .orElseThrow(() -> new ResourceNotFoundException("Week not found"));

                week.reject(reason);
        }

        // =====================================================
        // HOURS
        // =====================================================

        @Override
        public Double calculateApprovedHours(UUID tenantId, UUID internshipId) {

                internshipRepository
                                .findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Internship not found"));

                Double result = attendanceRepository.sumApprovedHours(
                                internshipId,
                                AttendanceStatus.APPROVED);

                return result != null ? result : 0.0;
        }
}