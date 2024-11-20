package com.citronix.citronix.service;

import com.citronix.citronix.dto.request.TreeRequestDTO;
import com.citronix.citronix.dto.response.TreeResponseDTO;
import com.citronix.citronix.service.comman.CrudService;
import org.springframework.stereotype.Service;

@Service
public interface TreeService extends CrudService<Long, TreeRequestDTO, TreeResponseDTO> {

}
