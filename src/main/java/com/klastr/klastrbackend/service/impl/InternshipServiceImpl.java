package com.klastr.klastrbackend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.klastr.klastrbackend.domain.internship.Internship;
import com.klastr.klastrbackend.domain.internship.InternshipStatus;
import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.domain.tenant.Tenant;
import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;
import com.klastr.klastrbackend.dto.internship.RegisterAttendanceRequest;
import com.klastr.klastrbackend.repository.InternshipRepository;
import com.klastr.klastrbackend.repository.OrganizationRepository;
import com.klastr.klastrbackend.repository.StudentRepository;
import com.klastr.klastrbackend.repository.TenantRepository;
import com.klastr.klastrbackend.service.InternshipService;

@Service
@RequiredArgsConstructor
@Transactional
public class InternshipServiceImpl implements InternshipService {

    private final InternshipRepository internshipRepository;
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

        Internship internship = Internship.builder()
                .tenant(tenant)
                .student(student)
                .organization(organization)
                .academicYear(request.getAcademicYear())
                .academicPeriod(request.getAcademicPeriod())
                .requiredHours(request.getRequiredHours())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(InternshipStatus.PENDING)
                .build();

        return mapToResponse(internshipRepository.save(internship));
    }

    // -------------------------------------------------
    // FIND
    // -------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public InternshipResponse findById(UUID tenantId, UUID internshipId) {

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        validateTenant(internship, tenantId);

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

        Internship internship = getInternshipOrThrow(internshipId, tenantId);

        if (internship.getStatus() != InternshipStatus.PENDING) {
            throw new RuntimeException("Only PENDING internships can be approved");
        }

        internship.setStatus(InternshipStatus.APPROVED);

        return mapToResponse(internshipRepository.save(internship));
    }

    @Override
    public InternshipResponse reject(UUID tenantId, UUID internshipId, String reason) {

        Internship internship = getInternshipOrThrow(internshipId, tenantId);

        if (internship.getStatus() != InternshipStatus.PENDING) {
            throw new RuntimeException("Only PENDING internships can be rejected");
        }

        internship.setStatus(InternshipStatus.REJECTED);

        return mapToResponse(internshipRepository.save(internship));
    }

    @Override
    public InternshipResponse activate(UUID tenantId, UUID internshipId) {

        Internship internship = getInternshipOrThrow(internshipId, tenantId);

        if (internship.getStatus() != InternshipStatus.APPROVED) {
            throw new RuntimeException("Only APPROVED internships can be activated");
        }

        internship.setStatus(InternshipStatus.ACTIVE);

        return mapToResponse(internshipRepository.save(internship));
    }

    @Override
    public InternshipResponse cancel(UUID tenantId, UUID internshipId, String reason) {

        Internship internship = getInternshipOrThrow(internshipId, tenantId);

        if (internship.getStatus() == InternshipStatus.COMPLETED) {
            throw new RuntimeException("Completed internships cannot be cancelled");
        }

        internship.setStatus(InternshipStatus.CANCELLED);

        return mapToResponse(internshipRepository.save(internship));
    }

    @Override
    public InternshipResponse complete(UUID tenantId, UUID internshipId) {

        Internship internship = getInternshipOrThrow(internshipId, tenantId);

        if (internship.getStatus() != InternshipStatus.ACTIVE) {
            throw new RuntimeException("Only ACTIVE internships can be completed");
        }

        internship.setStatus(InternshipStatus.COMPLETED);

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

    private Internship getInternshipOrThrow(UUID internshipId, UUID tenantId) {

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        validateTenant(internship, tenantId);

        return internship;
    }

    private void validateTenant(Internship internship, UUID tenantId) {

        if (!internship.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Internship does not belong to tenant");
        }
    }

    // -------------------------------------------------
    // MAPPER
    // -------------------------------------------------

    private InternshipResponse mapToResponse(Internship internship) {

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