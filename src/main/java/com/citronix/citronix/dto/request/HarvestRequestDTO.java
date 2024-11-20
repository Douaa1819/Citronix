package com.citronix.citronix.dto.request;

import com.citronix.citronix.entity.enums.Season;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HarvestRequestDTO {

    @NotNull(message = "Harvest date is required")
    private LocalDate harvestDate;

    @NotNull(message = "Season is required")
    private Season season;

    @NotNull(message = "Total quantity is required")
    @PositiveOrZero(message = "Total quantity cannot be negative")
    private Double totalQuantity;
}