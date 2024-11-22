package com.citronix.citronix.dto.response;

import java.util.List;

public record FieldResponseDTO(
        Long id,
        String name,
        Double area,
        List<TreeResponseDTO> trees,
        FarmFieldResponseDTO farm
) {

}
