package com.citronix.citronix.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FarmResponseDTO {

    private Long id;
    private String name;
    private String location;
    private Double totalArea;
    private LocalDate creationDate;
    //private List<Field> fieldIds;
}