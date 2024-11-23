package com.citronix.citronix.mapper;

import com.citronix.citronix.dto.request.FarmRequestDTO;
import com.citronix.citronix.dto.response.EmbeddedFieldFarmResponseDTO;
import com.citronix.citronix.dto.response.FarmResponseDTO;
import com.citronix.citronix.entity.Farm;
import com.citronix.citronix.entity.Field;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface FarmMapper {

    @Mapping(target = "fields", source = "fields")
    FarmResponseDTO toResponseDTO(Farm farm);


    Farm toEntity(FarmRequestDTO farmRequestDTO);

    default EmbeddedFieldFarmResponseDTO mapFieldToFieldFarmResponseDTO(Field field) {
        return new EmbeddedFieldFarmResponseDTO(field.getId(), field.getName(), field.getArea());
    }
    void updateFarmFromDto(FarmRequestDTO farmRequestDto, @MappingTarget Farm farm);
    }
