package com.citronix.citronix.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FarmRequestDTO {

    @NotBlank(message = "Name is required ")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Total area is required ")
    @Positive
    private Double totalArea;

    @NotNull
    @PastOrPresent(message = "The date must be in the past or in the present ")
    private LocalDate creationDate = LocalDate.now();

}
