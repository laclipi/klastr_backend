package com.klastr.klastrbackend.service.impl;

import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;
import com.klastr.klastrbackend.dto.internship.RegisterAttendanceRequest;
import com.klastr.klastrbackend.service.InternshipService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InternshipServiceImpl implements InternshipService {

    @Override
    public InternshipResponse create(UUID tenantId, CreateInternshipRequest request) {

        return InternshipResponse.builder()
                .id(UUID.randomUUID())
                .studentId(request.getStudentId())
                .organizationId(request.getOrganizationId())
                .academicYear(request.getAcademicYear())
                .academicPeriod(request.getAcademicPeriod())
                .requiredHours(request.getRequiredHours())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status("DRAFT")
                .build();
    }

    @Override
    public InternshipResponse findById(UUID tenantId, UUID internshipId) {

        return InternshipResponse.builder()
                .id(internshipId)
                .status("DRAFT")
                .build();
    }

    @Override
    public List<InternshipResponse> findByStudent(UUID tenantId, UUID studentId) {
        return List.of();
    }

    @Override
    public InternshipResponse approve(UUID tenantId, UUID internshipId) {
        return InternshipResponse.builder()
                .id(internshipId)
                .status("APPROVED")
                .build();
    }

    @Override
    public InternshipResponse reject(UUID tenantId, UUID internshipId, String reason) {
        return InternshipResponse.builder()
                .id(internshipId)
                .status("REJECTED")
                .build();
    }

    @Override
    public InternshipResponse activate(UUID tenantId, UUID internshipId) {
        return InternshipResponse.builder()
                .id(internshipId)
                .status("ACTIVE")
                .build();
    }

    @Override
    public InternshipResponse cancel(UUID tenantId, UUID internshipId, String reason) {
        return InternshipResponse.builder()
                .id(internshipId)
                .status("CANCELLED")
                .build();
    }

    @Override
    public InternshipResponse complete(UUID tenantId, UUID internshipId) {
        return InternshipResponse.builder()
                .id(internshipId)
                .status("COMPLETED")
                .build();
    }

    @Override
    public void registerAttendance(UUID tenantId, UUID internshipId, RegisterAttendanceRequest request) {
    }

    @Override
    public void submitWeek(UUID tenantId, UUID internshipId, UUID weekId) {
    }

    @Override
    public void approveWeek(UUID tenantId, UUID internshipId, UUID weekId) {
    }

    @Override
    public void rejectWeek(UUID tenantId, UUID internshipId, UUID weekId, String reason) {
    }

    @Override
    public Double calculateApprovedHours(UUID tenantId, UUID internshipId) {
        return 0.0;
    }
}