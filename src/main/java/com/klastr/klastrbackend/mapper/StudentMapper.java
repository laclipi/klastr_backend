package com.klastr.klastrbackend.mapper;

import org.springframework.stereotype.Component;

import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.dto.CreateStudentRequest;
import com.klastr.klastrbackend.dto.StudentResponse;

@Component
public class StudentMapper {

    public Student toEntity(CreateStudentRequest request, Organization organization) {
        return Student.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail().toLowerCase())
                .organization(organization)
                .build();
    }

    public StudentResponse toResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .organizationId(student.getOrganization().getId())
                .build();
    }
}