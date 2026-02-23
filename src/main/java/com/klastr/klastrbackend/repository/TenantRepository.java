package com.klastr.klastrbackend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klastr.klastrbackend.domain.tenant.Tenant;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}
