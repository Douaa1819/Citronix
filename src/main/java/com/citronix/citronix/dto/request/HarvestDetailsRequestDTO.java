package com.citronix.citronix.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public record HarvestDetailsRequestDTO (

    @NotNull(message = "Harvest ID is required")
     Long harvestId,

    @NotNull(message = "Tree ID is required")
     Long treeId,

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity cannot be negative")
     Double quantity
){
}