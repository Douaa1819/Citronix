package com.citronix.citronix.dto.response;

import com.citronix.citronix.entity.Tree;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldResponseDTO {
    private Long id;
    private String name;
    private Double area;
    private List<TreeResponseDTO> trees;
//    private int treeCount;
//    private Double treeDensity;

}