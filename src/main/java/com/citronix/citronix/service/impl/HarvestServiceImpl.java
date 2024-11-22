package com.citronix.citronix.service.impl;
import com.citronix.citronix.dto.request.HarvestRequestDTO;
import com.citronix.citronix.dto.response.HarvestResponseDTO;
import com.citronix.citronix.entity.Field;
import com.citronix.citronix.entity.Harvest;
import com.citronix.citronix.entity.HarvestDetails;
import com.citronix.citronix.entity.Tree;
import com.citronix.citronix.entity.enums.Season;
import com.citronix.citronix.mapper.HarvestMapper;
import com.citronix.citronix.repository.FieldRepository;
import com.citronix.citronix.repository.HarvestDetailsRepository;
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
@Transactional
@RequiredArgsConstructor
public class HarvestServiceImpl implements HarvestService {

    private final HarvestRepository harvestRepository;
    private final HarvestDetailsRepository harvestDetailsRepository;
    private final FieldRepository fieldRepository;
    private final HarvestMapper harvestMapper;


    @Override
    public List<HarvestResponseDTO> findAll() {
        return harvestRepository.findAll().stream()
                .map(harvest -> harvestMapper.toResponseDTO(harvest))
                .collect(Collectors.toList());
    }

    @Override
    public HarvestResponseDTO findById(Long id) {
        Harvest harvest = harvestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Harvest not found with ID: " + id));
        return harvestMapper.toResponseDTO(harvest);
    }

    @Override
    public HarvestResponseDTO create(HarvestRequestDTO harvestRequestDTO) {

        validateNewHarvest(harvestRequestDTO);

        if (harvestRequestDTO.fieldId() == null) {
            throw new IllegalArgumentException("Field ID cannot be null");
        }


        Field field = fieldRepository.findById(harvestRequestDTO.fieldId())
                .orElseThrow(() -> new EntityNotFoundException("Field not found with ID: " + harvestRequestDTO.fieldId()));


        Harvest harvest = harvestMapper.toEntity(harvestRequestDTO, field);

        Harvest savedHarvest = harvestRepository.save(harvest);


        populateHarvestDetails(savedHarvest, field.getTrees());

        return harvestMapper.toResponseDTO(savedHarvest);
    }



    @Override
    public HarvestResponseDTO update(Long id, HarvestRequestDTO harvestRequestDTO) {


        Harvest existingHarvest = harvestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Harvest not found with ID: " + id));


        validateSeasonForUpdate(harvestRequestDTO.season(), id);
        validateHarvestDate(harvestRequestDTO.harvestDate(), harvestRequestDTO.season());


        existingHarvest.setHarvestDate(harvestRequestDTO.harvestDate());
        existingHarvest.setSeason(harvestRequestDTO.season());


        Harvest updatedHarvest = harvestRepository.save(existingHarvest);


        harvestDetailsRepository.deleteByHarvestId(id);


        Field field = updatedHarvest.getField();
        List<Tree> trees = field.getTrees();


        populateHarvestDetails(updatedHarvest, trees);

        return harvestMapper.toResponseDTO(updatedHarvest);
    }


    @Override
    public void delete(Long id) {
        // Step 1: Find the harvest
        Harvest harvest = harvestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Harvest not found with ID: " + id));

        // Step 2: Delete harvest details and harvest
        harvestDetailsRepository.deleteByHarvestId(id);
        harvestRepository.delete(harvest);
    }

    private double populateHarvestDetails(Harvest harvest, List<Tree> trees) {
        double totalQuantity = 0.0;

        for (Tree tree : trees) {
            if (tree.getAge() > 20) continue;


            List<HarvestDetails> existingHarvestDetails = harvestDetailsRepository.findByTreeAndHarvestSeason(tree, harvest.getSeason());
            if (!existingHarvestDetails.isEmpty()) {
                continue;
            }

            double treeProductivity = tree.getProductivity();
            HarvestDetails harvestDetails = HarvestDetails.builder()
                    .harvest(harvest)
                    .tree(tree)
                    .quantity(treeProductivity)
                    .build();

            harvestDetailsRepository.save(harvestDetails);
            totalQuantity += treeProductivity;
        }

        return totalQuantity;
    }


    private void validateNewHarvest(HarvestRequestDTO requestDTO) {
        List<Harvest> existingHarvests = harvestRepository.findBySeason(requestDTO.season());

        if (!existingHarvests.isEmpty()) {
            for (Harvest existingHarvest : existingHarvests) {
                if (existingHarvest.getField().getId().equals(requestDTO.fieldId())) {
                    throw new IllegalStateException("A harvest already exists for this field in the specified season");
                }
            }
        }


        validateHarvestDate(requestDTO.harvestDate(), requestDTO.season());
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

        if (harvestDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Harvest date cannot be in the future");
        }

        if (!isDateMatchingSeason(harvestDate, season)) {
            throw new IllegalArgumentException("Harvest date does not match the specified season");
        }
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
}
