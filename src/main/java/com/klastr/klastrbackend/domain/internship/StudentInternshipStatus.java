package com.klastr.klastrbackend.domain.internship;

public enum StudentInternshipStatus {

    DRAFT, // Creado pero aún no iniciado oficialmente
    ACTIVE, // En curso
    COMPLETED, // Finalizado correctamente (horas cumplidas)
    CANCELLED; // Cancelado antes de finalizar

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isFinalState() {
        return this == COMPLETED || this == CANCELLED;
    }
}