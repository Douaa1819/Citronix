package com.citronix.citronix.mapper;

import com.citronix.citronix.dto.request.FieldRequestDTO;
import com.citronix.citronix.dto.response.EmbeddedFarmFieldResponseDTO;
import com.citronix.citronix.dto.response.FieldResponseDTO;
import com.citronix.citronix.dto.response.TreeResponseDTO;
import com.citronix.citronix.entity.Farm;
import com.citronix.citronix.entity.Field;
import com.citronix.citronix.entity.Tree;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FieldMapper {

    @Mapping(target = "trees", source = "trees")
    @Mapping(target = "farm", source = "farm")
    FieldResponseDTO toResponseDTO(Field field);
    Field toEntity(FieldRequestDTO fieldRequestDTO);




    TreeResponseDTO toTreeResponseDTO(Tree tree);
    List<TreeResponseDTO> toTreeResponseDTOs(List<Tree> trees);

    EmbeddedFarmFieldResponseDTO toResponse(Farm farm);



}
