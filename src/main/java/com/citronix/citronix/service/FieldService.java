package com.citronix.citronix.service;

import com.citronix.citronix.dto.request.FieldRequestDTO;
import com.citronix.citronix.dto.response.FieldResponseDTO;
import com.citronix.citronix.entity.Field;
import com.citronix.citronix.service.comman.CrudService;

public interface FieldService extends CrudService<Long, FieldRequestDTO, FieldResponseDTO> {
    Field findFieldById (Long id);
}