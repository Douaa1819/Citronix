package com.citronix.citronix.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FieldRequestDTO(
        @NotNull(message = "Name is required")
        @NotBlank
        String name,

        @NotNull(message = "Area is required ")
        @DecimalMin(value = "0.1", message = "Min area of a field is 0.1 hectare")
        Double area,

        @NotNull
        Long farmId
) {
}
