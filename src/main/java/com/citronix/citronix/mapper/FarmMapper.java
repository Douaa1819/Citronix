package com.citronix.citronix.mapper;

import com.citronix.citronix.dto.request.FarmRequestDTO;
import com.citronix.citronix.dto.response.FarmResponseDTO;
import com.citronix.citronix.entity.Farm;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface FarmMapper {

    FarmResponseDTO toResponseDTO(Farm farm);

    List<FarmResponseDTO> toResponseDTOs(List<Farm> farms);

    Farm toEntity(FarmRequestDTO farmRequestDTO);

    void updateFarmFromDto(FarmRequestDTO farmRequestDto, @MappingTarget Farm farm);

}