package com.klastr.klastrbackend.dto.internship;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InternshipResponse {

    private UUID id;

    private UUID studentId;
    private UUID organizationId;

    private Integer academicYear;
    private String academicPeriod;
    private Integer requiredHours;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status;
}