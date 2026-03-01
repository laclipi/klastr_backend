package com.klastr.klastrbackend.repository;

import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;
import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.domain.tenant.Tenant;

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
class StudentInternshipRepositoryIntegrationTest {

    @Autowired
    private StudentInternshipRepository internshipRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void shouldPersistAndLoadInternship() {

        // Arrange
        Tenant tenant = tenantRepository.save(
                Tenant.builder()
                        .name("Test Tenant")
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
                        .name("Test Org")
                        .build());

        StudentInternship internship = StudentInternship.builder()
                .tenant(tenant)
                .student(student)
                .organization(organization)
                .academicYear(2025) // Integer
                .academicPeriod("FIRST") // String
                .requiredHours(100) // Integer
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .build();

        // Act
        StudentInternship saved = internshipRepository.save(internship);

        StudentInternship found = internshipRepository
                .findById(saved.getId())
                .orElse(null);

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getAcademicYear()).isEqualTo(2025);
        assertThat(found.getRequiredHours()).isEqualTo(100);
        assertThat(found.getStudent().getEmail()).isEqualTo("john@test.com");
        assertThat(found.getOrganization().getName()).isEqualTo("Test Org");
    }
}