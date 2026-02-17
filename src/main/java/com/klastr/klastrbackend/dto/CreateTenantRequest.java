package com.klastr.klastrbackend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateTenantRequest {

    @NotBlank(message = "Name is required")
    private String name;
}
