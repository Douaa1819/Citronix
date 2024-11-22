package com.citronix.citronix.dto.response;


import java.time.LocalDate;


public record SaleResponseDTO(Long id,
                              Double quantity,
                              LocalDate saleDate,
                              Double prixUnitaire,
                              Double revenue,
                              String clientName,
                              LocalDate harvestDate) {
}
