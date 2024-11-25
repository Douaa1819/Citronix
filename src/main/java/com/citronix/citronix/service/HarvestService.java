package com.citronix.citronix.service;

import com.citronix.citronix.dto.request.HarvestRequestDTO;
import com.citronix.citronix.dto.request.TreeRequestDTO;
import com.citronix.citronix.dto.response.HarvestResponseDTO;
import com.citronix.citronix.dto.response.TreeResponseDTO;
import com.citronix.citronix.entity.Harvest;
import com.citronix.citronix.service.comman.CrudService;

public interface HarvestService extends CrudService<Long, HarvestRequestDTO, HarvestResponseDTO> {
}
