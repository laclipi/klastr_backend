package com.klastr.klastrbackend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klastr.klastrbackend.domain.student.Student;

public interface StudentRepository extends JpaRepository<Student, UUID> {

    List<Student> findByOrganizationId(UUID organizationId);

    List<Student> findByTenantId(UUID tenantId);

    java.util.Optional<Student> findByIdAndTenantId(UUID id, UUID tenantId);
}