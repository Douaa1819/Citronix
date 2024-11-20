package com.citronix.citronix.mapper;

import com.citronix.citronix.dto.request.FarmRequestDTO;
import com.citronix.citronix.dto.response.FarmResponseDTO;
import com.citronix.citronix.entity.Farm;
import com.citronix.citronix.entity.Field;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@Component
public interface FarmMapper {

    @Mapping(target = "fieldIds", expression = "java(mapFieldIds(farm.getFields()))")
    FarmResponseDTO toResponseDTO(Farm farm);

    List<FarmResponseDTO> toResponseDTOs(List<Farm> farms);

    Farm toEntity(FarmRequestDTO farmRequestDTO);

    void updateFarmFromDto(FarmRequestDTO farmRequestDto, @MappingTarget Farm farm);
    default List<Long> mapFieldIds(List<Field> fields) {
        if (fields == null) {
            return new ArrayList<>();
        }
        return fields.stream()
                .map(Field::getId)
                .collect(Collectors.toList());
    }
}