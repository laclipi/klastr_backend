package com.klastr.klastrbackend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klastr.klastrbackend.domain.internship.Internship;

public interface InternshipRepository
        extends JpaRepository<Internship, UUID> {

    List<Internship> findByTenant_IdAndStudent_Id(
            UUID tenantId,
            UUID studentId);
}