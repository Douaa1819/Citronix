package com.citronix.citronix.dto.response;

public record EmbedeedTreeFieldResponseDTO (

    Long id,
    String name,
    Double area,
    EmbeddedFarmFieldResponseDTO farm){
}
