package com.citronix.citronix.dto.response;


import java.time.LocalDate;


public record SaleResponseDTO (
     Long id,
     Double prixUnitaire,
     LocalDate saleDate,
     Double quantity,
     Double revenue,
     String clientName,
     Long harvestId,
     LocalDate harvestDate,
     String season
){

}