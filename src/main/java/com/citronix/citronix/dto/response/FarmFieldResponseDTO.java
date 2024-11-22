package com.citronix.citronix.dto.response;

import java.time.LocalDate;
import java.util.List;

public record FarmFieldResponseDTO(
    Long id,
    String name,
    String location,
    Double totalArea,
    LocalDate creationDate
)
{}
