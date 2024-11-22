package com.citronix.citronix.service.impl;

import com.citronix.citronix.dto.request.SaleRequestDTO;
import com.citronix.citronix.dto.response.SaleResponseDTO;
import com.citronix.citronix.entity.Harvest;
import com.citronix.citronix.entity.Sale;
import com.citronix.citronix.exception.EntityNotFoundException;
import com.citronix.citronix.mapper.SaleMapper;
import com.citronix.citronix.repository.HarvestRepository;
import com.citronix.citronix.repository.SaleRepository;
import com.citronix.citronix.service.SaleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final HarvestRepository harvestRepository;
    private final SaleMapper saleMapper;

    @Override
    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO requestDTO) {
        Sale sale = saleMapper.toEntity(requestDTO);

        Harvest harvest = harvestRepository.findById(requestDTO.harvestId())
                .orElseThrow(() -> new EntityNotFoundException("Harvest not found"));

        if (harvest.getTotalQuantity() < requestDTO.quantity()) {
            throw new IllegalArgumentException("Insufficient quantity available in harvest");
        }

        sale.setHarvest(harvest);
        harvest.setTotalQuantity(harvest.getTotalQuantity() - requestDTO.quantity());
        harvestRepository.save(harvest);

        Sale savedSale = saleRepository.save(sale);
        return saleMapper.toDTO(savedSale);
    }

    @Override
    public SaleResponseDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale not found"));
        return saleMapper.toDTO(sale);
    }

    @Override
    public List<SaleResponseDTO> getAllSales() {
        return saleRepository.findAll().stream()
                .map(saleMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SaleResponseDTO updateSale(Long id, SaleRequestDTO requestDTO) {
        Sale existingSale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale not found"));

        Double quantityDifference = requestDTO.quantity() - existingSale.getQuantity();

        Harvest harvest = harvestRepository.findById(requestDTO.harvestId())
                .orElseThrow(() -> new EntityNotFoundException("Harvest not found"));

        if (harvest.getTotalQuantity() < quantityDifference) {
            throw new IllegalArgumentException("Insufficient quantity available in harvest");
        }

        existingSale.setPrixUnitaire(requestDTO.prixUnitaire());
        existingSale.setSaleDate(requestDTO.saleDate());
        existingSale.setQuantity(requestDTO.prixUnitaire());
        existingSale.setClientName(requestDTO.clientName());

        harvest.setTotalQuantity(harvest.getTotalQuantity() - quantityDifference);
        harvestRepository.save(harvest);

        Sale updatedSale = saleRepository.save(existingSale);
        return saleMapper.toDTO(updatedSale);
    }

    @Override
@Transactional
   public void deleteSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale not found"));
        Harvest harvest = sale.getHarvest();
        harvest.setTotalQuantity(harvest.getTotalQuantity() + sale.getQuantity());
        harvestRepository.save(harvest);

        saleRepository.delete(sale);
   }

    @Override
    public Double calculateRevenue(Long harvestId) {
        List<Sale> sales = saleRepository.findByHarvestId(harvestId);

        return sales.stream()
                .mapToDouble(sale -> sale.getQuantity() * sale.getPrixUnitaire())
                .sum();
    }
}