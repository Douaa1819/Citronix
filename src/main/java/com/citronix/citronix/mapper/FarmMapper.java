package com.citronix.citronix.mapper;

import com.citronix.citronix.dto.request.FarmRequestDTO;
import com.citronix.citronix.dto.response.FarmFieldResponseDTO;
import com.citronix.citronix.dto.response.FarmResponseDTO;
import com.citronix.citronix.dto.response.FieldFarmResponseDTO;
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

    @Mapping(target = "fields", source = "fields")
    FarmResponseDTO toResponseDTO(Farm farm);


    Farm toEntity(FarmRequestDTO farmRequestDTO);

    default FieldFarmResponseDTO mapFieldToFieldFarmResponseDTO(Field field) {
        return new FieldFarmResponseDTO(field.getId(), field.getName(), field.getArea());
    }
    void updateFarmFromDto(FarmRequestDTO farmRequestDto, @MappingTarget Farm farm);
    }
