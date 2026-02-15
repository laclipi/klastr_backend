package com.klastr.klastrbackend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klastr.klastrbackend.domain.Tenant;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
}
