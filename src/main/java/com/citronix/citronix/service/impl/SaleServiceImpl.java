package com.citronix.citronix.service.impl;

import com.citronix.citronix.dto.request.SaleRequestDTO;
import com.citronix.citronix.dto.response.SaleResponseDTO;
import com.citronix.citronix.entity.Harvest;
import com.citronix.citronix.entity.HarvestDetails;
import com.citronix.citronix.entity.Sale;
import com.citronix.citronix.common.exception.EntityNotFoundException;
import com.citronix.citronix.mapper.SaleMapper;
import com.citronix.citronix.repository.HarvestRepository;
import com.citronix.citronix.repository.SaleRepository;
import com.citronix.citronix.service.SaleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final HarvestRepository harvestRepository;
    private final SaleMapper saleMapper;

    @Override
    public SaleResponseDTO create(SaleRequestDTO requestDTO) {
        Sale sale = saleMapper.toEntity(requestDTO);

        Harvest harvest = harvestRepository.findById(requestDTO.harvestId())
                .orElseThrow(() -> new EntityNotFoundException("Harvest ",requestDTO.harvestId()));


        double actualTotalQuantity = calculateTotalQuantity(harvest);


        if (actualTotalQuantity < requestDTO.quantity()) {
            throw new IllegalArgumentException("Insufficient quantity available in harvest: " + actualTotalQuantity);
        }


        sale.setHarvest(harvest);
        harvest.setTotalQuantity(actualTotalQuantity - requestDTO.quantity());
        harvestRepository.save(harvest);


        Sale savedSale = saleRepository.save(sale);
        return saleMapper.toDTO(savedSale);
    }

    public double calculateTotalQuantity(Harvest harvest) {
        return harvest.getHarvestDetails().stream()
                .mapToDouble(HarvestDetails::getQuantity)
                .sum();
    }
    @Override
    public SaleResponseDTO findById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale",id));
        return saleMapper.toDTO(sale);
    }

    @Override
    public Page<SaleResponseDTO>findAll(
            int page,
            int size
    ) {
        return saleRepository.findAll(PageRequest.of(page, size))
                .map(saleMapper::toDTO);
    }

    @Override
    public SaleResponseDTO update(Long id, SaleRequestDTO requestDTO) {
        Sale existingSale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale",id));

        Double quantityDifference = requestDTO.quantity() - existingSale.getQuantity();

        Harvest harvest = harvestRepository.findById(requestDTO.harvestId())
                .orElseThrow(() -> new EntityNotFoundException("Harvest",id));

        if (harvest.getTotalQuantity() < quantityDifference) {
            throw new IllegalArgumentException("Insufficient quantity available in harvest"+ harvest.getTotalQuantity());
        }

        existingSale.setPrixUnitaire(requestDTO.prixUnitaire());
        existingSale.setSaleDate(requestDTO.saleDate());
        existingSale.setQuantity(requestDTO.prixUnitaire());
        existingSale.setClientName(requestDTO.clientName());

        harvest.setTotalQuantity(harvest.getTotalQuantity() - quantityDifference);
        harvestRepository.save(harvest);


        return saleMapper.toDTO(existingSale);
    }

    @Override
@Transactional
   public void delete(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sale",id));
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