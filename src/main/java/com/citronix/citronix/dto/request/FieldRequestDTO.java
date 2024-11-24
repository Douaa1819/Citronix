package com.citronix.citronix.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FieldRequestDTO(
        @NotNull(message = "Name is required")
        @NotBlank
        String name,

        @NotNull(message = "Area is required ")
        @DecimalMin(value = "1000", message = "Minimum area of a field is 1,000 m²")
        Double area,

        @NotNull
        Long farmId
) {
}
