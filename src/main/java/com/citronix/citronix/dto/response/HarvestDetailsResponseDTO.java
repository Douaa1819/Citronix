package com.citronix.citronix.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public record HarvestDetailsResponseDTO (

     Long harvestId,
     Long treeId,
     Double quantity
)
{}