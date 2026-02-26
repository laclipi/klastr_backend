package com.klastr.klastrbackend.domain.internship.agreement;

public enum AgreementStatus {

    DRAFT,
    PENDING_SIGNATURE,
    ACTIVE,
    SUSPENDED,
    CLOSED,
    CANCELLED;

    public boolean canTransitionTo(AgreementStatus target) {

        return switch (this) {
            case DRAFT -> target == PENDING_SIGNATURE || target == CANCELLED;
            case PENDING_SIGNATURE -> target == ACTIVE || target == CANCELLED;
            case ACTIVE -> target == SUSPENDED || target == CLOSED;
            case SUSPENDED -> target == ACTIVE;
            case CLOSED, CANCELLED -> false;
        };
    }
}