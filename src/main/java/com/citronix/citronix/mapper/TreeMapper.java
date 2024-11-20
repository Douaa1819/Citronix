package com.citronix.citronix.mapper;

import com.citronix.citronix.dto.request.TreeRequestDTO;
import com.citronix.citronix.dto.response.TreeResponseDTO;
import com.citronix.citronix.entity.Tree;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TreeMapper {

    TreeResponseDTO toResponseDTO(Tree tree);

    List<TreeResponseDTO> toResponseDTOs(List<Tree> trees);

    Tree toEntity(TreeRequestDTO treeRequestDTO);

}