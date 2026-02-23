package com.klastr.klastrbackend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.student.Student;
import com.klastr.klastrbackend.domain.tenant.Tenant;
import com.klastr.klastrbackend.dto.CreateStudentRequest;
import com.klastr.klastrbackend.dto.StudentResponse;
import com.klastr.klastrbackend.exception.BusinessException;
import com.klastr.klastrbackend.exception.ResourceNotFoundException;
import com.klastr.klastrbackend.mapper.StudentMapper;
import com.klastr.klastrbackend.repository.OrganizationRepository;
import com.klastr.klastrbackend.repository.StudentRepository;
import com.klastr.klastrbackend.repository.TenantRepository;
import com.klastr.klastrbackend.service.StudentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

        private final StudentRepository studentRepository;
        private final TenantRepository tenantRepository;
        private final OrganizationRepository organizationRepository;
        private final StudentMapper studentMapper;

        @Override
        public StudentResponse create(UUID tenantId, CreateStudentRequest request) {

                Tenant tenant = tenantRepository.findById(tenantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

                Organization organization = organizationRepository.findById(request.getOrganizationId())
                                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

                if (!organization.getTenant().getId().equals(tenantId)) {
                        throw new BusinessException(
                                        "Organization does not belong to tenant",
                                        HttpStatus.BAD_REQUEST);
                }

                Student student = studentMapper.toEntity(request, organization);
                student.setTenant(tenant);

                Student saved = studentRepository.save(student);

                return studentMapper.toResponse(saved);
        }

        @Override
        public StudentResponse findById(UUID tenantId, UUID studentId) {

                Student student = studentRepository.findByIdAndTenantId(studentId, tenantId)
                                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

                return studentMapper.toResponse(student);
        }

        @Override
        public List<StudentResponse> findByOrganization(UUID tenantId, UUID organizationId) {

                return studentRepository.findByOrganizationId(organizationId)
                                .stream()
                                .filter(student -> student.getTenant().getId().equals(tenantId))
                                .map(studentMapper::toResponse)
                                .toList();
        }
}