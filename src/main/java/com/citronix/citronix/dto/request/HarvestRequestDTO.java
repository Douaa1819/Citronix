package com.citronix.citronix.dto.request;

import com.citronix.citronix.entity.enums.Season;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record HarvestRequestDTO(
    @NotNull(message = "Harvest date is required")
     LocalDate harvestDate,

    @NotNull(message = "Season is required")
     Season season,

        @NotNull(message = "Field ID is required")
            Long fieldId

)
{
}