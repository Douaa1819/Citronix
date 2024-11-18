package com.citronix.citronix.service;

import com.citronix.citronix.dto.request.FarmRequestDTO;
import com.citronix.citronix.dto.response.FarmResponseDTO;
import com.citronix.citronix.service.generic.CrudService;
import org.springframework.stereotype.Service;

@Service
public interface FarmService extends CrudService<Long, FarmRequestDTO, FarmResponseDTO> {
}