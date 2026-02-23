package com.klastr.klastrbackend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.klastr.klastrbackend.domain.Organization;
import com.klastr.klastrbackend.domain.Student;
import com.klastr.klastrbackend.domain.Tenant;
import com.klastr.klastrbackend.domain.User;
import com.klastr.klastrbackend.domain.UserRole;
import com.klastr.klastrbackend.dto.CreateStudentRequest;
import com.klastr.klastrbackend.dto.StudentResponse;
import com.klastr.klastrbackend.exception.BusinessException;
import com.klastr.klastrbackend.exception.ResourceNotFoundException;
import com.klastr.klastrbackend.mapper.StudentMapper;
import com.klastr.klastrbackend.repository.OrganizationRepository;
import com.klastr.klastrbackend.repository.StudentRepository;
import com.klastr.klastrbackend.repository.TenantRepository;
import com.klastr.klastrbackend.repository.UserRepository;
import com.klastr.klastrbackend.service.StudentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final TenantRepository tenantRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final StudentMapper studentMapper;

    @Override
    public StudentResponse create(UUID tenantId, CreateStudentRequest request) {

        // 1️⃣ Validar tenant
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Tenant not found with id: " + tenantId)
                );

        // 2️⃣ Validar organization
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(()
                        -> new ResourceNotFoundException("Organization not found")
                );

        if (!organization.getTenant().getId().equals(tenantId)) {
            throw new BusinessException(
                    "Organization does not belong to tenant",
                    HttpStatus.BAD_REQUEST
            );
        }

        // 3️⃣ Validar user
        User user = userRepository.findByIdAndTenantId(request.getUserId(), tenantId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("User not found")
                );

        // 4️⃣ Verificar role STUDENT
        if (user.getRole() != UserRole.STUDENT) {
            throw new BusinessException(
                    "User must have STUDENT role",
                    HttpStatus.BAD_REQUEST
            );
        }

        // 5️⃣ Verificar que no tenga Student ya
        if (studentRepository.findByUserIdAndTenantId(user.getId(), tenantId).isPresent()) {
            throw new BusinessException(
                    "This user is already registered as student",
                    HttpStatus.CONFLICT
            );
        }

        // 6️⃣ Verificar expediente único
        if (studentRepository.existsByAcademicRecordNumberAndTenantId(
                request.getAcademicRecordNumber(),
                tenantId)) {

            throw new BusinessException(
                    "Academic record number already exists in this tenant",
                    HttpStatus.CONFLICT
            );
        }

        Student student = studentMapper.toEntity(request, user, organization);

        student.setTenant(tenant);

        Student saved = studentRepository.save(student);

        return studentMapper.toResponse(saved);
    }

    @Override
    public StudentResponse findById(UUID tenantId, UUID studentId) {

        Student student = studentRepository.findByIdAndTenantId(studentId, tenantId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Student not found")
                );

        return studentMapper.toResponse(student);
    }

    @Override
    public List<StudentResponse> findByOrganization(UUID tenantId, UUID organizationId) {

        return studentRepository.findByOrganizationIdAndTenantId(organizationId, tenantId)
                .stream()
                .map(studentMapper::toResponse)
                .toList();
    }
}
