package com.klastr.klastrbackend.dto.internship;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAttendanceRequest {

    @NotNull
    private LocalDate date;

    @NotNull
    @Positive(message = "Hours must be greater than 0")
    @Max(value = 12, message = "Hours cannot exceed 12 per day")
    private Integer hours;
}