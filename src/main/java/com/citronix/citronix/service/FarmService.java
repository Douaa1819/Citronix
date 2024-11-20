package com.citronix.citronix.service;

import com.citronix.citronix.dto.request.FarmRequestDTO;
import com.citronix.citronix.dto.response.FarmResponseDTO;
import com.citronix.citronix.service.comman.CrudService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FarmService extends CrudService<Long, FarmRequestDTO, FarmResponseDTO> {
    List<FarmResponseDTO> findFarmsByCriteria(String name, String location);

}