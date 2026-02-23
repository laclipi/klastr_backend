package com.klastr.klastrbackend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.klastr.klastrbackend.domain.organization.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    @Query("""
           SELECT o
           FROM Organization o
           JOIN FETCH o.tenant
           WHERE o.tenant.id = :tenantId
           """)
    List<Organization> findByTenantIdWithTenant(@Param("tenantId") UUID tenantId);

    boolean existsByNameAndTenantId(String name, UUID tenantId);

    Optional<Organization> findByIdAndTenantId(UUID id, UUID tenantId);
}
