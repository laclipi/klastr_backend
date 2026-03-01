package com.klastr.klastrbackend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

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
import com.klastr.klastrbackend.repository.InternshipAttendanceRepository;
import com.klastr.klastrbackend.repository.InternshipAttendanceWeekRepository;
import com.klastr.klastrbackend.repository.OrganizationRepository;
import com.klastr.klastrbackend.repository.StudentInternshipRepository;
import com.klastr.klastrbackend.repository.StudentRepository;
import com.klastr.klastrbackend.repository.TenantRepository;

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

                // -------------------------
                // Arrange
                // -------------------------

                Tenant tenant = tenantRepository.save(
                                Tenant.builder()
                                                .name("Test Tenant")
                                                .build());

                Organization organization = organizationRepository.save(
                                Organization.builder()
                                                .name("Test Org")
                                                .tenant(tenant)
                                                .build());

                Student student = studentRepository.save(
                                Student.builder()
                                                .tenant(tenant)
                                                .organization(organization)
                                                .firstName("John")
                                                .lastName("Doe")
                                                .email("john.doe@test.com")
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

                // DRAFT → APPROVED → ACTIVE
                internship.approve();
                internship.activate();
                internshipRepository.save(internship);

                RegisterAttendanceRequest attendanceRequest = new RegisterAttendanceRequest(
                                LocalDate.now().minusDays(1),
                                8);

                internshipService.registerAttendance(
                                tenant.getId(),
                                internship.getId(),
                                attendanceRequest);

                InternshipAttendanceWeek week = weekRepository.findAll().get(0);

                internshipService.submitWeek(
                                tenant.getId(),
                                internship.getId(),
                                week.getId());

                // -------------------------
                // Act
                // -------------------------

                internshipService.approveWeek(
                                tenant.getId(),
                                internship.getId(),
                                week.getId());

                // -------------------------
                // Assert
                // -------------------------

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
}