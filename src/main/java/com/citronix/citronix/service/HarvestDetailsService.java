package com.citronix.citronix.service;

import com.citronix.citronix.dto.response.HarvestDetailsResponseDTO;
import com.citronix.citronix.entity.HarvestDetails;
import com.citronix.citronix.repository.HarvestDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HarvestDetailsService {

    private final HarvestDetailsRepository harvestDetailsRepository;

    public List<HarvestDetailsResponseDTO> getAllHarvestDetails() {
        return harvestDetailsRepository.findAll().stream()
                .map(detail -> new HarvestDetailsResponseDTO(
                        detail.getHarvest().getId(),
                        detail.getTree().getId(),
                        detail.getQuantity()
                ))
                .collect(Collectors.toList());
    }



    public void deleteByHarvestId(Long harvestId) {
        harvestDetailsRepository.deleteByHarvestId(harvestId);
    }
}