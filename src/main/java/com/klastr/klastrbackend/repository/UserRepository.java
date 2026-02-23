package com.klastr.klastrbackend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klastr.klastrbackend.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Email único por tenant
    boolean existsByEmailAndTenantId(String email, UUID tenantId);

    // Para login futuro
    Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

    // Seguridad estricta multi-tenant
    Optional<User> findByIdAndTenantId(UUID id, UUID tenantId);

    // Listado por organization
    List<User> findByOrganizationId(UUID organizationId);
}
