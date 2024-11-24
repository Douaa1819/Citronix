package com.citronix.citronix.mapper;

import com.citronix.citronix.dto.request.TreeRequestDTO;
import com.citronix.citronix.dto.response.TreeResponseDTO;
import com.citronix.citronix.entity.Tree;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TreeMapper {

    @Mapping(target = "age", expression = "java(tree.getAge())")
    @Mapping(target = "productivite", expression = "java(tree.getProductivity())")
    TreeResponseDTO toResponseDTO(Tree tree);

    Tree toEntity(TreeRequestDTO treeRequestDTO);

}