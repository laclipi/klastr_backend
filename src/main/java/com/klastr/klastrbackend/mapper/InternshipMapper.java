package com.klastr.klastrbackend.mapper;

import org.springframework.stereotype.Component;

import com.klastr.klastrbackend.domain.internship.lifecycle.StudentInternship;
import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.dto.internship.CreateInternshipRequest;
import com.klastr.klastrbackend.dto.internship.StudentInternshipResponse;

@Component
public class InternshipMapper {

    public StudentInternship toEntity(
            CreateInternshipRequest request,
            Student student,
            Organization organization) {

        return StudentInternship.builder()
                .student(student)
                .organization(organization)
                .academicYear(request.getAcademicYear())
                .academicPeriod(request.getAcademicPeriod())
                .requiredHours(request.getRequiredHours())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
    }

    public StudentInternshipResponse toResponse(StudentInternship internship) {

        StudentInternshipResponse response = new StudentInternshipResponse();
        response.setId(internship.getId());
        response.setStudentId(internship.getStudent().getId());
        response.setOrganizationId(internship.getOrganization().getId());
        response.setStartDate(internship.getStartDate());
        response.setEndDate(internship.getEndDate());
        response.setRequiredHours(internship.getRequiredHours());
        response.setApprovedHours(null); // ajusta si lo gestionas
        response.setStatus(internship.getStatus());

        return response;
    }
}