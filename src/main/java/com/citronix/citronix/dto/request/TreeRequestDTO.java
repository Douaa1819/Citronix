package com.citronix.citronix.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public record TreeRequestDTO(
        @NotNull(message = "Planting date is required")
        @PastOrPresent(message = "Planting date must be in the past or present")
        LocalDate plantingDate,
        Long fieldId
) {

}
