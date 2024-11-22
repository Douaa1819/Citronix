package com.citronix.citronix.mapper;
import com.citronix.citronix.dto.request.HarvestDetailsRequestDTO;
import com.citronix.citronix.dto.request.HarvestRequestDTO;
import com.citronix.citronix.dto.response.HarvestDetailsResponseDTO;
import com.citronix.citronix.dto.response.HarvestResponseDTO;
import com.citronix.citronix.entity.Field;
import com.citronix.citronix.entity.Harvest;
import com.citronix.citronix.entity.HarvestDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(
        componentModel = "spring"
)
public interface HarvestMapper {

    Harvest toEntity(HarvestRequestDTO dto);

    HarvestResponseDTO toResponseDTO(Harvest harvest);
}