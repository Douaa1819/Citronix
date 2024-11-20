package com.citronix.citronix.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TreeResponseDTO {
    private Long id;
    private LocalDate plantingDate;
    private Integer age;
    private String productivityCategory;
    private Double expectedYieldPerSeason;
}

