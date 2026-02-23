package com.klastr.klastrbackend.dto.internship;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class RegisterAttendanceRequest {

    @NotNull
    private UUID internshipId;

    @NotNull
    private LocalDate date;

    @NotNull
    private Integer hours;

    public UUID getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(UUID internshipId) {
        this.internshipId = internshipId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }
}