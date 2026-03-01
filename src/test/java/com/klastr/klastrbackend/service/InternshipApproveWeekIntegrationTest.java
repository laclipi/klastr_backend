package com.klastr.klastrbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.klastr.klastrbackend.domain.internship.attendance.AttendanceStatus;
import com.klastr.klastrbackend.domain.internship.attendance.InternshipAttendance;
import com.klastr.klastrbackend.domain.internship.attendance.InternshipAttendanceWeek;
import com.klastr.klastrbackend.domain.internship.attendance.WeekStatus;
import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;
import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.domain.tenant.Tenant;
import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.RegisterAttendanceRequest;
import com.klastr.klastrbackend.exception.BusinessException;
import com.klastr.klastrbackend.repository.*;

@SpringBootTest
@ActiveProfiles("test")
class InternshipApproveWeekIntegrationTest {

        @Autowired
        private InternshipService internshipService;

        @Autowired
        private TenantRepository tenantRepository;

        @Autowired
        private OrganizationRepository organizationRepository;

        @Autowired
        private StudentRepository studentRepository;

        @Autowired
        private StudentInternshipRepository internshipRepository;

        @Autowired
        private InternshipAttendanceWeekRepository weekRepository;

        @Autowired
        private InternshipAttendanceRepository attendanceRepository;

        @Test
        void shouldApproveWeekAndAllAttendances() {

                Tenant tenant = tenantRepository.save(
                                Tenant.builder()
                                                .name("Test Tenant " + UUID.randomUUID())
                                                .build());

                Organization organization = organizationRepository.save(
                                Organization.builder()
                                                .name("Test Org " + UUID.randomUUID())
                                                .tenant(tenant)
                                                .build());

                Student student = studentRepository.save(
                                Student.builder()
                                                .tenant(tenant)
                                                .organization(organization)
                                                .firstName("John")
                                                .lastName("Doe")
                                                .email("john.doe+" + UUID.randomUUID() + "@test.com")
                                                .build());

                CreateInternshipRequest createRequest = CreateInternshipRequest.builder()
                                .studentId(student.getId())
                                .organizationId(organization.getId())
                                .academicYear(2025)
                                .academicPeriod("SPRING")
                                .requiredHours(20)
                                .startDate(LocalDate.now().minusDays(5))
                                .endDate(LocalDate.now().plusDays(30))
                                .build();

                var response = internshipService.create(tenant.getId(), createRequest);

                StudentInternship internship = internshipRepository
                                .findById(response.getId())
                                .orElseThrow();

                internship.approve();
                internship.activate();
                internshipRepository.save(internship);

                internshipService.registerAttendance(
                                tenant.getId(),
                                internship.getId(),
                                new RegisterAttendanceRequest(
                                                LocalDate.now().minusDays(1),
                                                8));

                InternshipAttendanceWeek week = weekRepository
                                .findByInternship_Id(internship.getId())
                                .get(0);

                // SUBMIT
                internshipService.submitWeek(
                                tenant.getId(),
                                internship.getId(),
                                week.getId());

                // APPROVE
                internshipService.approveWeek(
                                tenant.getId(),
                                internship.getId(),
                                week.getId());

                InternshipAttendanceWeek updatedWeek = weekRepository
                                .findById(week.getId())
                                .orElseThrow();

                assertThat(updatedWeek.getStatus())
                                .isEqualTo(WeekStatus.APPROVED);

                List<InternshipAttendance> attendances = attendanceRepository
                                .findAllByWeek_IdAndWeek_Internship_Tenant_Id(
                                                week.getId(),
                                                tenant.getId());

                assertThat(attendances)
                                .isNotEmpty()
                                .allMatch(a -> a.getStatus() == AttendanceStatus.APPROVED);
        }

        @Test
        void should_not_allow_register_attendance_when_week_is_submitted() {

                Tenant tenant = tenantRepository.save(
                                Tenant.builder()
                                                .name("Test Tenant " + UUID.randomUUID())
                                                .build());

                Organization organization = organizationRepository.save(
                                Organization.builder()
                                                .name("Test Org " + UUID.randomUUID())
                                                .tenant(tenant)
                                                .build());

                Student student = studentRepository.save(
                                Student.builder()
                                                .tenant(tenant)
                                                .organization(organization)
                                                .firstName("John")
                                                .lastName("Doe")
                                                .email("john.doe+" + UUID.randomUUID() + "@test.com")
                                                .build());

                CreateInternshipRequest createRequest = CreateInternshipRequest.builder()
                                .studentId(student.getId())
                                .organizationId(organization.getId())
                                .academicYear(2025)
                                .academicPeriod("SPRING")
                                .requiredHours(20)
                                .startDate(LocalDate.now().minusDays(5))
                                .endDate(LocalDate.now().plusDays(30))
                                .build();

                var response = internshipService.create(tenant.getId(), createRequest);

                StudentInternship internship = internshipRepository
                                .findById(response.getId())
                                .orElseThrow();

                internship.approve();
                internship.activate();
                internshipRepository.save(internship);

                internshipService.registerAttendance(
                                tenant.getId(),
                                internship.getId(),
                                new RegisterAttendanceRequest(
                                                LocalDate.now().minusDays(1),
                                                8));

                InternshipAttendanceWeek week = weekRepository
                                .findByInternship_Id(internship.getId())
                                .get(0);

                internshipService.submitWeek(
                                tenant.getId(),
                                internship.getId(),
                                week.getId());

                assertThrows(BusinessException.class, () -> internshipService.registerAttendance(
                                tenant.getId(),
                                internship.getId(),
                                new RegisterAttendanceRequest(
                                                LocalDate.now().minusDays(2),
                                                4)));
        }
}