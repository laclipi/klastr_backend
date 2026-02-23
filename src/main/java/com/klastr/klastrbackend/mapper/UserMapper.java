package com.klastr.klastrbackend.mapper;

import org.springframework.stereotype.Component;

import com.klastr.klastrbackend.domain.organization.Organization;
import com.klastr.klastrbackend.domain.user.User;
import com.klastr.klastrbackend.domain.user.UserRole;
import com.klastr.klastrbackend.domain.user.UserStatus;
import com.klastr.klastrbackend.dto.CreateUserRequest;
import com.klastr.klastrbackend.dto.UpdateUserRequest;
import com.klastr.klastrbackend.dto.UserResponse;

@Component
public class UserMapper {

    // CREATE → Entity
    public User toEntity(CreateUserRequest request,
            Organization organization,
            UserRole role,
            String encodedPassword) {

        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail().toLowerCase())
                .password(encodedPassword)
                .role(role)
                .status(UserStatus.ACTIVE)
                .organization(organization)
                .build();
    }

    // UPDATE → mutates entity
    public void updateEntity(User user,
            UpdateUserRequest request,
            UserRole role,
            UserStatus status,
            String encodedPassword) {

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail().toLowerCase());

        if (encodedPassword != null) {
            user.setPassword(encodedPassword);
        }

        if (role != null) {
            user.setRole(role);
        }

        if (status != null) {
            user.setStatus(status);
        }
    }

    // ENTITY → RESPONSE
    public UserResponse toResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .tenantId(user.getTenant().getId())
                .organizationId(user.getOrganization().getId())
                .build();
    }
}
