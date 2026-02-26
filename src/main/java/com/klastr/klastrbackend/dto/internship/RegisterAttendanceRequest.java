package com.klastr.klastrbackend.dto.internship;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;

public class RegisterAttendanceRequest {

    @NotNull
    private LocalDate date;

    @NotNull
    @Positive(message = "Hours must be greater than 0")
    @Max(value = 12, message = "Hours cannot exceed 12 per day")
    private Integer hours;

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