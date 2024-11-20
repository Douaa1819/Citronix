package com.citronix.citronix.dto.response;


import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

@Builder
public record FarmResponseDTO(
        Long id,
        String name,
        String location,
        Double totalArea,
        LocalDate creationDate,
        List<Long> fieldIds
) {
}
