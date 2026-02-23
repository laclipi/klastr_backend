package com.klastr.klastrbackend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;
import com.klastr.klastrbackend.dto.attendance.RegisterAttendanceRequest;

public interface InternshipService {

    // -------------------------------------------------
    // CREACIÓN
    // -------------------------------------------------
    InternshipResponse create(UUID tenantId, CreateInternshipRequest request);

    // -------------------------------------------------
    // CONSULTA
    // -------------------------------------------------
    InternshipResponse findById(UUID tenantId, UUID internshipId);

    List<InternshipResponse> findByStudent(UUID tenantId, UUID studentId);

    // -------------------------------------------------
    // FLUJO DE ESTADO
    // -------------------------------------------------
    InternshipResponse approve(UUID tenantId, UUID internshipId);

    InternshipResponse reject(UUID tenantId, UUID internshipId, String reason);

    InternshipResponse activate(UUID tenantId, UUID internshipId);

    InternshipResponse cancel(UUID tenantId, UUID internshipId, String reason);

    InternshipResponse complete(UUID tenantId, UUID internshipId);

    // -------------------------------------------------
    // ASISTENCIA
    // -------------------------------------------------
    void registerAttendance(
            UUID tenantId,
            UUID internshipId,
            RegisterAttendanceRequest request
    );

    void submitWeek(
            UUID tenantId,
            UUID internshipId,
            UUID weekId
    );

    void approveWeek(
            UUID tenantId,
            UUID internshipId,
            UUID weekId
    );

    void rejectWeek(
            UUID tenantId,
            UUID internshipId,
            UUID weekId,
            String reason
    );

    Double calculateApprovedHours(UUID tenantId, UUID internshipId);
}
