package com.klastr.klastrbackend.domain.internship.lifecycle;

public enum StudentInternshipStatus {

    DRAFT,
    APPROVED,
    REJECTED,
    ACTIVE,
    COMPLETED,
    CANCELLED;

    public boolean isFinalState() {
        return this == COMPLETED || this == CANCELLED || this == REJECTED;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }
}