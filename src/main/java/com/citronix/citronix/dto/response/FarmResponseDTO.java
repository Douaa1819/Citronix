package com.citronix.citronix.dto.response;
import java.time.LocalDate;
import java.util.List;

public record FarmResponseDTO(
        Long id,
        String name,
        String location,
        Double totalArea,
        LocalDate creationDate,
       List<FieldFarmResponseDTO> fields
) {

}
