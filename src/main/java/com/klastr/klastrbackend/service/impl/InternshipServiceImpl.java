package com.klastr.klastrbackend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;
import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternshipStatus;
import com.klastr.klastrbackend.domain.tenant.Tenant;
import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;
import com.klastr.klastrbackend.dto.internship.RegisterAttendanceRequest;
import com.klastr.klastrbackend.repository.StudentInternshipRepository;
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
                .status(StudentInternshipStatus.DRAFT)
                .build();

        return mapToResponse(internshipRepository.save(internship));
    }

    // -------------------------------------------------
    // FIND
    // -------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public InternshipResponse findById(UUID tenantId, UUID internshipId) {

        StudentInternship internship = getInternshipOrThrow(internshipId, tenantId);

        return mapToResponse(internship);
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

    // -------------------------------------------------
    // STATE FLOW
    // -------------------------------------------------

    @Override
    public InternshipResponse approve(UUID tenantId, UUID internshipId) {

        StudentInternship internship = getInternshipOrThrow(internshipId, tenantId);

        if (internship.getStatus() != StudentInternshipStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT internships can be approved");
        }

        internship.setStatus(StudentInternshipStatus.APPROVED);

        return mapToResponse(internshipRepository.save(internship));
    }

    @Override
    public InternshipResponse reject(UUID tenantId, UUID internshipId, String reason) {

        StudentInternship internship = getInternshipOrThrow(internshipId, tenantId);

        if (internship.getStatus() != StudentInternshipStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT internships can be rejected");
        }

        internship.setStatus(StudentInternshipStatus.REJECTED);

        return mapToResponse(internshipRepository.save(internship));
    }

    @Override
    public InternshipResponse activate(UUID tenantId, UUID internshipId) {

        StudentInternship internship = getInternshipOrThrow(internshipId, tenantId);

        if (internship.getStatus() != StudentInternshipStatus.APPROVED) {
            throw new RuntimeException("Only APPROVED internships can be activated");
        }

        internship.setStatus(StudentInternshipStatus.ACTIVE);

        return mapToResponse(internshipRepository.save(internship));
    }

    @Override
    public InternshipResponse cancel(UUID tenantId, UUID internshipId, String reason) {

        StudentInternship internship = getInternshipOrThrow(internshipId, tenantId);

        if (internship.getStatus() == StudentInternshipStatus.COMPLETED) {
            throw new RuntimeException("Completed internships cannot be cancelled");
        }

        internship.setStatus(StudentInternshipStatus.CANCELLED);

        return mapToResponse(internshipRepository.save(internship));
    }

    @Override
    public InternshipResponse complete(UUID tenantId, UUID internshipId) {

        StudentInternship internship = getInternshipOrThrow(internshipId, tenantId);

        if (internship.getStatus() != StudentInternshipStatus.ACTIVE) {
            throw new RuntimeException("Only ACTIVE internships can be completed");
        }

        internship.setStatus(StudentInternshipStatus.COMPLETED);

        return mapToResponse(internshipRepository.save(internship));
    }

    // -------------------------------------------------
    // ATTENDANCE (PHASE 2)
    // -------------------------------------------------

    @Override
    public void registerAttendance(UUID tenantId, UUID internshipId, RegisterAttendanceRequest request) {
        throw new UnsupportedOperationException("Attendance not implemented yet");
    }

    @Override
    public void submitWeek(UUID tenantId, UUID internshipId, UUID weekId) {
        throw new UnsupportedOperationException("Attendance not implemented yet");
    }

    @Override
    public void approveWeek(UUID tenantId, UUID internshipId, UUID weekId) {
        throw new UnsupportedOperationException("Attendance not implemented yet");
    }

    @Override
    public void rejectWeek(UUID tenantId, UUID internshipId, UUID weekId, String reason) {
        throw new UnsupportedOperationException("Attendance not implemented yet");
    }

    @Override
    public Double calculateApprovedHours(UUID tenantId, UUID internshipId) {
        throw new UnsupportedOperationException("Attendance not implemented yet");
    }

    // -------------------------------------------------
    // INTERNAL HELPERS
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

    // -------------------------------------------------
    // MAPPER
    // -------------------------------------------------

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