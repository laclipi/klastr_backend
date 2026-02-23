package com.klastr.klastrbackend.dto.internship;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInternshipRequest {

    @NotNull
    private UUID studentId;

    @NotNull
    private UUID organizationId;

    @NotNull
    private Integer academicYear;

    @NotNull
    @Size(max = 20)
    private String academicPeriod;

    @NotNull
    @Positive
    private Integer requiredHours;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}