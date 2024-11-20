package com.citronix.citronix.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HarvestDetailsResponseDTO {

    private Long harvestId;
    private Long treeId;
    private Double quantity;
}