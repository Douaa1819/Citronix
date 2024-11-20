package com.citronix.citronix.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record FarmRequestDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Location is required")
        String location,

        @NotNull(message = "Total area is required")
        @Positive
        Double totalArea,

        @NotNull(message = "The date must be in the past or in the present")
        @PastOrPresent
        LocalDate creationDate
) {
}
