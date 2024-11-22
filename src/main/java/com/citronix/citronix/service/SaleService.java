package com.citronix.citronix.service;

import com.citronix.citronix.dto.request.SaleRequestDTO;
import com.citronix.citronix.dto.response.SaleResponseDTO;
import com.citronix.citronix.entity.Sale;
import com.citronix.citronix.service.comman.CrudService;

import java.util.List;

public interface SaleService {

    SaleResponseDTO createSale(SaleRequestDTO requestDTO);

    SaleResponseDTO getSaleById(Long id);

    List<SaleResponseDTO> getAllSales();

    SaleResponseDTO updateSale(Long id, SaleRequestDTO requestDTO);

    void deleteSale(Long id);
    Double calculateRevenue(Long harvestId);
}
