package com.klastr.klastrbackend.domain.internship;

public enum InternshipStatus {

    PENDING,
    APPROVED,
    ACTIVE,
    REJECTED,
    CANCELLED,
    COMPLETED;

    public boolean isActive() {
        return this == ACTIVE;
    }
}