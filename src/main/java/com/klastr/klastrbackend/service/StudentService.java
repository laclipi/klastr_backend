package com.klastr.klastrbackend.service;

import java.util.List;
import java.util.UUID;

import com.klastr.klastrbackend.dto.CreateStudentRequest;
import com.klastr.klastrbackend.dto.StudentResponse;

public interface StudentService {

    StudentResponse create(UUID tenantId, CreateStudentRequest request);

    StudentResponse findById(UUID tenantId, UUID studentId);

    List<StudentResponse> findByOrganization(UUID tenantId, UUID organizationId);
}