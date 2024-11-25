package com.citronix.citronix.service;

import com.citronix.citronix.dto.request.SaleRequestDTO;
import com.citronix.citronix.dto.response.SaleResponseDTO;
import com.citronix.citronix.entity.Sale;
import com.citronix.citronix.service.comman.CrudService;

import java.util.List;

public interface SaleService extends CrudService<Long,SaleRequestDTO,SaleResponseDTO> {

    Double calculateRevenue(Long harvestId);
}
