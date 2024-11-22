package com.citronix.citronix.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record SaleRequestDTO (
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
         Double prixUnitaire,

    @NotNull(message = "Sale date is required")
         LocalDate saleDate,

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
         Double quantity,

    @NotNull(message = "Harvest ID is required")
         Long harvestId,

    @NotNull(message = "Client name is required")
         String clientName
){

}