package com.citronix.citronix.dto.response;

import java.time.LocalDate;

public record EmbeddedFarmFieldResponseDTO(
    Long id,
    String name,
    String location,
    Double totalArea,
    LocalDate creationDate
)
{}
