package com.citronix.citronix.service;

import com.citronix.citronix.dto.request.HarvestRequestDTO;
import com.citronix.citronix.dto.response.HarvestResponseDTO;
import com.citronix.citronix.service.comman.CrudService;

public interface HarvestDetailsService extends CrudService<Long, HarvestRequestDTO, HarvestResponseDTO> {
}