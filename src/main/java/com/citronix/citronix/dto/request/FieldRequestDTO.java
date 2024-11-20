package com.citronix.citronix.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FieldRequestDTO {

    @NotNull(message = "Name is required")
    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @NotNull(message = "Area is required ")
    @DecimalMin(value = "0.1", message = "Min area of a field it is 0.1 hectare")
    private Double area;

    @NotNull
    private Long farmId;
}