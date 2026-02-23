package com.klastr.klastrbackend.mapper;

import org.springframework.stereotype.Component;

import com.klastr.klastrbackend.domain.internship.Internship;
import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.InternshipResponse;

@Component
public class InternshipMapper {

    public Internship toEntity(
            CreateInternshipRequest request,
            Student student,
            Organization organization) {

        return Internship.builder()
                .student(student)
                .organization(organization)
                .academicYear(request.getAcademicYear())
                .academicPeriod(request.getAcademicPeriod())
                .requiredHours(request.getRequiredHours())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
    }

    public InternshipResponse toResponse(Internship internship) {

        return InternshipResponse.builder()
                .id(internship.getId())
                .studentId(internship.getStudent().getId())
                .organizationId(internship.getOrganization().getId())
                .academicYear(internship.getAcademicYear())
                .academicPeriod(internship.getAcademicPeriod())
                .requiredHours(internship.getRequiredHours())
                .startDate(internship.getStartDate())
                .endDate(internship.getEndDate())
                .status(internship.getStatus().name())
                .build();
    }
}