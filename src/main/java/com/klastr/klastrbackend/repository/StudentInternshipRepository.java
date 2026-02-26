package com.klastr.klastrbackend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klastr.klastrbackend.domain.internship.StudentInternship;

public interface StudentInternshipRepository extends JpaRepository<StudentInternship, UUID> {

}
