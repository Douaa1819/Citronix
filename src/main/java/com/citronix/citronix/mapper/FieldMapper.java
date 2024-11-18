package com.citronix.citronix.mapper;

import com.citronix.citronix.dto.request.FieldRequestDTO;
import com.citronix.citronix.dto.response.FieldResponseDTO;
import com.citronix.citronix.dto.response.TreeResponseDTO;
import com.citronix.citronix.entity.Field;
import com.citronix.citronix.entity.Tree;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FieldMapper {
    // Mapping for Field -> FieldResponseDTO
    @Mapping(target = "trees", source = "trees")
    FieldResponseDTO toResponseDTO(Field field);

    // Mapping for List<Field> -> List<FieldResponseDTO>
    List<FieldResponseDTO> toResponseDTOs(List<Field> fields);

    // Mapping for FieldRequestDTO -> Field
    Field toEntity(FieldRequestDTO fieldRequestDTO);

    // Mapping for Tree -> TreeResponseDTO
    TreeResponseDTO toTreeResponseDTO(Tree tree);

    // Mapping for List<Tree> -> List<TreeResponseDTO>
    List<TreeResponseDTO> toTreeResponseDTOs(List<Tree> trees);
}
