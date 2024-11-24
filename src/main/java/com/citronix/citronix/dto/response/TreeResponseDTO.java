package com.citronix.citronix.dto.response;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record TreeResponseDTO(
        Long id,
        LocalDate plantingDate,
        Integer age,
        Double productivite,
        EmbedeedTreeFieldResponseDTO field
) {


}
