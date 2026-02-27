package com.klastr.klastrbackend.service;

import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.domain.tenant.Tenant;
import com.klastr.klastrbackend.domain.internship.attendance.InternshipAttendanceWeek;
import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;
import com.klastr.klastrbackend.dto.internship.RegisterAttendanceRequest;
import com.klastr.klastrbackend.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InternshipServiceIntegrationTest {

    @Autowired
    private InternshipService internshipService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private InternshipAttendanceWeekRepository weekRepository;

    // ============================================================
    // HAPPY PATH: FULL LIFECYCLE
    // ============================================================

    @Test
    void shouldCompleteInternshipWhenApprovedHoursAreEnough() {

        // ---------- Arrange ----------

        Tenant tenant = tenantRepository.save(
                Tenant.builder()
                        .name("Tenant A")
                        .build());

        Student student = studentRepository.save(
                Student.builder()
                        .tenant(tenant)
                        .firstName("John")
                        .lastName("Doe")
                        .email("john@test.com")
                        .build());

        Organization organization = organizationRepository.save(
                Organization.builder()
                        .tenant(tenant)
                        .name("Org A")
                        .build());

        CreateInternshipRequest request = new CreateInternshipRequest();
        request.setStudentId(student.getId());
        request.setOrganizationId(organization.getId());
        request.setAcademicYear(2025);
        request.setAcademicPeriod("FIRST");
        request.setRequiredHours(8);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusMonths(1));

        InternshipResponse created = internshipService.create(tenant.getId(), request);

        // ---------- Lifecycle transitions ----------

        internshipService.approve(tenant.getId(), created.getId());
        internshipService.activate(tenant.getId(), created.getId());

        // ---------- Register attendance ----------

        RegisterAttendanceRequest attendanceRequest = new RegisterAttendanceRequest();
        attendanceRequest.setDate(LocalDate.now());
        attendanceRequest.setHours(8);

        internshipService.registerAttendance(
                tenant.getId(),
                created.getId(),
                attendanceRequest);

        // ---------- Submit + Approve week ----------

        InternshipAttendanceWeek week = weekRepository.findAll().get(0);

        internshipService.submitWeek(
                tenant.getId(),
                created.getId(),
                week.getId());

        internshipService.approveWeek(
                tenant.getId(),
                created.getId(),
                week.getId());

        // ---------- Complete internship ----------

        InternshipResponse completed = internshipService.complete(
                tenant.getId(),
                created.getId());

        // ---------- Assert ----------

        assertThat(completed.getStatus()).isEqualTo("COMPLETED");
    }
}