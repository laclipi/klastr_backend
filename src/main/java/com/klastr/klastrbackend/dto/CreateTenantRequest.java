package com.klastr.klastrbackend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class CreateTenantRequest {

    //  Evita null, vacío o solo espacios
    @NotBlank(message = "Name is required")

    //  Evita nombres ridículos tipo "a"
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")

    private String name;
}
