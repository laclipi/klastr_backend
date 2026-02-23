package com.klastr.klastrbackend.domain.internship;

public enum InternshipStatus {

    PENDING, // Creada, pendiente aprobación
    REJECTED, // Rechazada
    APPROVED, // Aprobada
    ACTIVE, // En curso
    COMPANY_EVALUATED,// Empresa evaluó
    FAILED, // No superada
    COMPLETED, // Superada y validada
    CANCELLED         // Cancelada administrativamente
}
