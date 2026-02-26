package com.klastr.klastrbackend.domain.internship.attendance;

public enum AttendanceStatus {

    PENDING,
    APPROVED,
    REJECTED;

    public boolean isApproved() {
        return this == APPROVED;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }
}