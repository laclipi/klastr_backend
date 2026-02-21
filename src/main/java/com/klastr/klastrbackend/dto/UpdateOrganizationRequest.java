package com.klastr.klastrbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrganizationRequest {

    @NotBlank
    private String name;
}
