package com.klastr.klastrbackend.service;

import java.util.List;
import java.util.UUID;

import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;
import com.klastr.klastrbackend.dto.internship.RegisterAttendanceRequest;

public interface InternshipService {

        // -------------------------------------------------
        // CREATE
        // -------------------------------------------------
        InternshipResponse create(
                        UUID tenantId,
                        CreateInternshipRequest request);

        // -------------------------------------------------
        // FIND
        // -------------------------------------------------
        InternshipResponse findById(
                        UUID tenantId,
                        UUID internshipId);

        List<InternshipResponse> findByStudent(
                        UUID tenantId,
                        UUID studentId);

        // -------------------------------------------------
        // STATE FLOW
        // -------------------------------------------------
        InternshipResponse approve(
                        UUID tenantId,
                        UUID internshipId);

        InternshipResponse reject(
                        UUID tenantId,
                        UUID internshipId,
                        String reason);

        InternshipResponse activate(
                        UUID tenantId,
                        UUID internshipId);

        InternshipResponse cancel(
                        UUID tenantId,
                        UUID internshipId,
                        String reason);

        InternshipResponse complete(
                        UUID tenantId,
                        UUID internshipId);

        // -------------------------------------------------
        // ATTENDANCE
        // -------------------------------------------------
        void registerAttendance(
                        UUID tenantId,
                        UUID internshipId,
                        RegisterAttendanceRequest request);

        void submitWeek(
                        UUID tenantId,
                        UUID internshipId,
                        UUID weekId);

        void approveWeek(
                        UUID tenantId,
                        UUID internshipId,
                        UUID weekId);

        void rejectWeek(
                        UUID tenantId,
                        UUID internshipId,
                        UUID weekId,
                        String reason);

        Double calculateApprovedHours(
                        UUID tenantId,
                        UUID internshipId);
}