package com.citronix.citronix.service.impl;
import com.citronix.citronix.dto.request.HarvestRequestDTO;
import com.citronix.citronix.dto.response.HarvestDetailsResponseDTO;
import com.citronix.citronix.dto.response.HarvestResponseDTO;
import com.citronix.citronix.entity.Harvest;
import com.citronix.citronix.entity.enums.Season;
import com.citronix.citronix.mapper.HarvestMapper;
import com.citronix.citronix.repository.HarvestRepository;
import com.citronix.citronix.service.HarvestService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class HarvestServiceImpl implements HarvestService {

    private final HarvestRepository harvestRepository;
    private final HarvestMapper harvestMapper;


    @Override
    public HarvestResponseDTO create(HarvestRequestDTO requestDTO) {
        validateNewHarvest(requestDTO);

        Harvest harvest = harvestMapper.toEntity(requestDTO);
        harvest = harvestRepository.save(harvest);

        return harvestMapper.toResponseDTO(harvest);
    }

    @Override
    public HarvestResponseDTO update(Long id, HarvestRequestDTO requestDTO) {
        Harvest existingHarvest = harvestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Harvest not found with id: " + id));

        if (!existingHarvest.getSeason().equals(requestDTO.getSeason())) {
            validateSeasonForUpdate(requestDTO.getSeason(), id);
        }

        validateHarvestDate(requestDTO.getHarvestDate(), requestDTO.getSeason());

        existingHarvest.setHarvestDate(requestDTO.getHarvestDate());
        existingHarvest.setSeason(requestDTO.getSeason());
        existingHarvest.setTotalQuantity(requestDTO.getTotalQuantity());

        existingHarvest = harvestRepository.save(existingHarvest);
        return harvestMapper.toResponseDTO(existingHarvest);
    }

    @Override
    public void delete(Long id) {
        if (!harvestRepository.existsById(id)) {
            throw new EntityNotFoundException("Harvest not found with id: " + id);
        }
        harvestRepository.deleteById(id);
    }

    @Override
    public HarvestResponseDTO findById(Long id) {
        return harvestRepository.findById(id)
                .map(harvestMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Harvest not found with id: " + id));
    }

    @Override
    public List<HarvestResponseDTO> findAll() {
        List<Harvest> harvests = harvestRepository.findAll();

        return harvests.stream()
                .map(harvest -> {
                    List<HarvestDetailsResponseDTO> detailsResponseDTOs = harvest.getHarvestDetails().stream()
                            .map(harvestMapper::toResponseDTO)
                            .collect(Collectors.toList());

                    HarvestResponseDTO responseDTO = harvestMapper.toResponseDTO(harvest);
                    responseDTO.setHarvestDetails(detailsResponseDTOs);
                    return responseDTO;
                })
                .collect(Collectors.toList());
    }


    private void validateNewHarvest(HarvestRequestDTO requestDTO) {
        List<Harvest> existingHarvests = harvestRepository.findBySeason(requestDTO.getSeason());
        if (!existingHarvests.isEmpty()) {
            throw new IllegalStateException("A harvest already exists for this season");
        }

        validateHarvestDate(requestDTO.getHarvestDate(), requestDTO.getSeason());
    }

    private boolean isDateMatchingSeason(LocalDate date, Season season) {
        int month = date.getMonthValue();
        return switch (season) {
            case WINTER -> month == 12 || month == 1 || month == 2;
            case SPRING -> month >= 3 && month <= 5;
            case SUMMER -> month >= 6 && month <= 8;
            case AUTUMN -> month >= 9 && month <= 11;
        };
    }

    private void validateSeasonForUpdate(Season newSeason, Long currentHarvestId) {
        List<Harvest> existingHarvests = harvestRepository.findBySeason(newSeason);

        boolean seasonExistsForOtherHarvest = existingHarvests.stream()
                .anyMatch(harvest -> !harvest.getId().equals(currentHarvestId));

        if (seasonExistsForOtherHarvest) {
            throw new IllegalStateException("A harvest already exists for this season");
        }
    }

    private void validateHarvestDate(LocalDate harvestDate, Season season) {
        if (harvestDate == null) {
            throw new IllegalArgumentException("Harvest date cannot be null");
        }

        if (harvestDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Harvest date cannot be in the future");
        }

        if (!isDateMatchingSeason(harvestDate, season)) {
            throw new IllegalArgumentException("Harvest date does not match the specified season");
        }
    }
}
