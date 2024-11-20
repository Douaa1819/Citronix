package com.citronix.citronix.mapper;
import com.citronix.citronix.dto.request.HarvestDetailsRequestDTO;
import com.citronix.citronix.dto.request.HarvestRequestDTO;
import com.citronix.citronix.dto.response.HarvestDetailsResponseDTO;
import com.citronix.citronix.dto.response.HarvestResponseDTO;
import com.citronix.citronix.entity.Harvest;
import com.citronix.citronix.entity.HarvestDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface HarvestMapper {

    @Mapping(target = "harvestDetails", source = "harvestDetails")
    HarvestResponseDTO toResponseDTO(Harvest harvest);

    Harvest toEntity(HarvestRequestDTO requestDTO);

    @Mapping(target = "harvestId", source = "details.id.harvestId")
    @Mapping(target = "treeId", source = "details.id.treeId")
    HarvestDetailsResponseDTO toResponseDTO(HarvestDetails details);

    HarvestDetails toEntity(HarvestDetailsRequestDTO requestDTO);

    List<HarvestDetailsResponseDTO> toHarvestDetailsResponseDTOList(List<HarvestDetails> details);
}