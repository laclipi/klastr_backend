package com.klastr.klastrbackend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.klastr.klastrbackend.domain.internship.attendance.InternshipAttendanceWeek;
import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;
import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternshipStatus;
import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.domain.tenant.Tenant;
import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.RegisterAttendanceRequest;
import com.klastr.klastrbackend.repository.InternshipAttendanceWeekRepository;
import com.klastr.klastrbackend.repository.OrganizationRepository;
import com.klastr.klastrbackend.repository.StudentInternshipRepository;
import com.klastr.klastrbackend.repository.StudentRepository;
import com.klastr.klastrbackend.repository.TenantRepository;

@SpringBootTest
@ActiveProfiles("test")
class InternshipServiceIntegrationTest {

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

        @Test
        void shouldCompleteInternshipWhenApprovedHoursAreEnough() {

                // -------------------------
                // Arrange
                // -------------------------

                Tenant tenant = tenantRepository.save(
                                Tenant.builder()
                                                .name("Tenant-" + UUID.randomUUID())
                                                .build());

                Organization organization = organizationRepository.save(
                                Organization.builder()
                                                .name("Organization-" + UUID.randomUUID())
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
                                .requiredHours(8)
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

                // Register 8 hours
                RegisterAttendanceRequest attendanceRequest = new RegisterAttendanceRequest(
                                LocalDate.now().minusDays(1),
                                8);

                internshipService.registerAttendance(
                                tenant.getId(),
                                internship.getId(),
                                attendanceRequest);

                // 🔥 Obtener la week correcta (no usar findAll().get(0))
                InternshipAttendanceWeek week = weekRepository
                                .findByInternship_Id(internship.getId())
                                .stream()
                                .findFirst()
                                .orElseThrow();

                // OPEN → SUBMITTED
                internshipService.submitWeek(
                                tenant.getId(),
                                internship.getId(),
                                week.getId());

                // SUBMITTED → APPROVED
                internshipService.approveWeek(
                                tenant.getId(),
                                internship.getId(),
                                week.getId());

                // Ahora completar internship
                internshipService.complete(
                                tenant.getId(),
                                internship.getId());

                // -------------------------
                // Assert
                // -------------------------

                StudentInternship updatedInternship = internshipRepository.findById(internship.getId()).orElseThrow();

                assertThat(updatedInternship.getStatus())
                                .isEqualTo(StudentInternshipStatus.COMPLETED);
        }
}