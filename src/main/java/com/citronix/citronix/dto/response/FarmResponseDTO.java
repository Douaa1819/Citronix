package com.citronix.citronix.dto.response;

import com.citronix.citronix.entity.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
