package com.klastr.klastrbackend.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.klastr.klastrbackend.domain.internship.attendance.*;
import com.klastr.klastrbackend.domain.internship.lifecycle.*;
import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;
import com.klastr.klastrbackend.dto.internship.RegisterAttendanceRequest;
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
                                .orElseThrow();

                Organization organization = organizationRepository
                                .findById(request.getOrganizationId())
                                .orElseThrow();

                StudentInternship internship = internshipMapper.toEntity(request, student, organization);

                // 🔥 CLAVE: asignar tenant
                internship.setTenant(student.getTenant());

                internship = internshipRepository.save(internship);

                return internshipMapper.toResponse(internship);
        }

        // =====================================================
        // FIND
        // =====================================================

        @Override
        public InternshipResponse findById(UUID tenantId, UUID internshipId) {

                StudentInternship internship = internshipRepository.findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow();

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

                StudentInternship internship = internshipRepository.findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow();

                internship.approve();

                return internshipMapper.toResponse(internship);
        }

        @Override
        public InternshipResponse reject(UUID tenantId, UUID internshipId, String reason) {

                StudentInternship internship = internshipRepository.findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow();

                internship.reject();

                return internshipMapper.toResponse(internship);
        }

        @Override
        public InternshipResponse activate(UUID tenantId, UUID internshipId) {

                StudentInternship internship = internshipRepository.findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow();

                internship.activate();

                return internshipMapper.toResponse(internship);
        }

        @Override
        public InternshipResponse cancel(UUID tenantId, UUID internshipId, String reason) {

                StudentInternship internship = internshipRepository.findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow();

                internship.cancel();

                return internshipMapper.toResponse(internship);
        }

        @Override
        public InternshipResponse complete(UUID tenantId, UUID internshipId) {

                StudentInternship internship = internshipRepository.findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow();

                internship.complete();

                return internshipMapper.toResponse(internship);
        }

        // =====================================================
        // ATTENDANCE
        // =====================================================

        @Override
        public void registerAttendance(UUID tenantId, UUID internshipId, RegisterAttendanceRequest request) {

                StudentInternship internship = internshipRepository.findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow();

                LocalDate date = request.getDate();

                if (attendanceRepository.existsByInternship_IdAndDate(internshipId, date)) {
                        throw new IllegalStateException("Attendance already registered for this date");
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
                weekRepository.findById(weekId).orElseThrow().submit();
        }

        @Override
        public void approveWeek(UUID tenantId, UUID internshipId, UUID weekId) {

                InternshipAttendanceWeek week = weekRepository.findById(weekId).orElseThrow();

                internshipRepository.findByIdAndTenant_Id(internshipId, tenantId)
                                .orElseThrow();

                week.approve("Approved");

                attendanceRepository.findAllByWeek_Id(weekId)
                                .forEach(a -> a.setStatus(AttendanceStatus.APPROVED));
        }

        @Override
        public void rejectWeek(UUID tenantId, UUID internshipId, UUID weekId, String reason) {
                weekRepository.findById(weekId).orElseThrow().reject(reason);
        }

        // =====================================================
        // HOURS
        // =====================================================

        @Override
        public Double calculateApprovedHours(UUID tenantId, UUID internshipId) {

                Double result = attendanceRepository.sumApprovedHours(
                                internshipId,
                                AttendanceStatus.APPROVED);

                return result != null ? result : 0.0;
        }
}