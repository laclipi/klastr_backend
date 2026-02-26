package com.klastr.klastrbackend.domain.internship.attendance;

public enum WeekStatus {

    OPEN, // Se pueden registrar días
    SUBMITTED, // Enviada para revisión
    APPROVED, // Semana aprobada
    REJECTED; // Semana rechazada

    public boolean isClosed() {
        return this == APPROVED;
    }

    public boolean isOpen() {
        return this == OPEN;
    }
}