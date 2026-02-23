package com.klastr.klastrbackend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.klastr.klastrbackend.domain.Organization;
import com.klastr.klastrbackend.domain.Tenant;
import com.klastr.klastrbackend.domain.User;
import com.klastr.klastrbackend.domain.UserRole;
import com.klastr.klastrbackend.domain.UserStatus;
import com.klastr.klastrbackend.dto.CreateUserRequest;
import com.klastr.klastrbackend.dto.UpdateUserRequest;
import com.klastr.klastrbackend.dto.UserResponse;
import com.klastr.klastrbackend.exception.BusinessException;
import com.klastr.klastrbackend.exception.ResourceNotFoundException;
import com.klastr.klastrbackend.mapper.UserMapper;
import com.klastr.klastrbackend.repository.OrganizationRepository;
import com.klastr.klastrbackend.repository.TenantRepository;
import com.klastr.klastrbackend.repository.UserRepository;
import com.klastr.klastrbackend.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final OrganizationRepository organizationRepository;
    private final UserMapper userMapper;

    // CREATE
    @Override
    public UserResponse create(UUID tenantId, CreateUserRequest request) {

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Tenant not found with id: " + tenantId)
                );

        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(()
                        -> new ResourceNotFoundException("Organization not found with id: " + request.getOrganizationId())
                );

        if (!organization.getTenant().getId().equals(tenantId)) {
            throw new BusinessException(
                    "Organization does not belong to tenant",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (userRepository.existsByEmailAndTenantId(
                request.getEmail().toLowerCase(),
                tenantId)) {

            throw new BusinessException(
                    "User with this email already exists in this tenant",
                    HttpStatus.CONFLICT
            );
        }

        UserRole role;
        try {
            role = UserRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid role", HttpStatus.BAD_REQUEST);
        }

        // ⚠ Aquí luego irá passwordEncoder.encode(...)
        String encodedPassword = request.getPassword();

        User user = userMapper.toEntity(
                request,
                organization,
                role,
                encodedPassword
        );

        user.setTenant(tenant);

        User saved = userRepository.save(user);

        return userMapper.toResponse(saved);
    }

    // UPDATE
    @Override
    public UserResponse update(UUID tenantId, UUID userId, UpdateUserRequest request) {

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("User not found")
                );

        if (userRepository.existsByEmailAndTenantId(
                request.getEmail().toLowerCase(),
                tenantId)
                && !user.getEmail().equalsIgnoreCase(request.getEmail())) {

            throw new BusinessException(
                    "User with this email already exists in this tenant",
                    HttpStatus.CONFLICT
            );
        }

        UserRole role = null;
        if (request.getRole() != null) {
            try {
                role = UserRole.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Invalid role", HttpStatus.BAD_REQUEST);
            }
        }

        UserStatus status = null;
        if (request.getStatus() != null) {
            try {
                status = UserStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Invalid status", HttpStatus.BAD_REQUEST);
            }
        }

        String encodedPassword = null;
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            encodedPassword = request.getPassword(); // luego encode
        }

        userMapper.updateEntity(user, request, role, status, encodedPassword);

        User updated = userRepository.save(user);

        return userMapper.toResponse(updated);
    }

    // FIND BY ID
    @Override
    public UserResponse findById(UUID tenantId, UUID userId) {

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("User not found")
                );

        return userMapper.toResponse(user);
    }

    // FIND BY ORGANIZATION
    @Override
    public List<UserResponse> findByOrganization(UUID tenantId, UUID organizationId) {

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("Organization not found")
                );

        if (!organization.getTenant().getId().equals(tenantId)) {
            throw new BusinessException(
                    "Organization does not belong to tenant",
                    HttpStatus.BAD_REQUEST
            );
        }

        return userRepository.findByOrganizationId(organizationId)
                .stream()
                .filter(user -> user.getTenant().getId().equals(tenantId))
                .map(userMapper::toResponse)
                .toList();
    }

    // SOFT DELETE
    @Override
    public void deactivate(UUID tenantId, UUID userId) {

        User user = userRepository.findByIdAndTenantId(userId, tenantId)
                .orElseThrow(()
                        -> new ResourceNotFoundException("User not found")
                );

        user.setStatus(UserStatus.INACTIVE);

        userRepository.save(user);
    }
}
