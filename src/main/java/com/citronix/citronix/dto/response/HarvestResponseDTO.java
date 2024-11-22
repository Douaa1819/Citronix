package com.citronix.citronix.dto.response;
import com.citronix.citronix.entity.enums.Season;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record HarvestResponseDTO (

     Long id,
     LocalDate harvestDate,
     Season season,
     Long fieldId

){

}