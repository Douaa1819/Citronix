package com.citronix.citronix.service.impl;
import com.citronix.citronix.dto.request.HarvestRequestDTO;
import com.citronix.citronix.dto.response.HarvestResponseDTO;
import com.citronix.citronix.entity.*;
import com.citronix.citronix.entity.enums.Season;
import com.citronix.citronix.mapper.HarvestMapper;
import com.citronix.citronix.repository.FieldRepository;
import com.citronix.citronix.repository.HarvestDetailsRepository;
import com.citronix.citronix.repository.HarvestRepository;
import com.citronix.citronix.service.HarvestService;
import com.citronix.citronix.common.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public Page<HarvestResponseDTO> findAll(int page, int size) {
        return harvestRepository.findAll(PageRequest.of(page,size))
                .map( harvestMapper::toResponseDTO);
    }


    @Override
    public HarvestResponseDTO findById(Long id) {
        Harvest harvest = harvestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Harvest" , id));
        return harvestMapper.toResponseDTO(harvest);
    }


    @Override
    public HarvestResponseDTO create(HarvestRequestDTO harvestRequestDTO) {

        Field field = fieldRepository.findById(harvestRequestDTO.fieldId())
                .orElseThrow(() -> new EntityNotFoundException("Field " , harvestRequestDTO.fieldId()));


        Harvest harvest = harvestMapper.toEntity(harvestRequestDTO);
        validateNewHarvest(harvestRequestDTO);
        populateHarvestDetails(harvest, field.getTrees());
        Harvest savedHarvest = harvestRepository.save(harvest);



        return harvestMapper.toResponseDTO(savedHarvest);
    }



    @Override
    public HarvestResponseDTO update(Long id, HarvestRequestDTO harvestRequestDTO) {
        Harvest existingHarvest = harvestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Harvest" , id));


        validateSeasonForUpdate(harvestRequestDTO.season(), id);


        validateHarvestDate(harvestRequestDTO.harvestDate(), harvestRequestDTO.season());


        Field field = fieldRepository.findById(harvestRequestDTO.fieldId())
                .orElseThrow(() -> new EntityNotFoundException("Field" , harvestRequestDTO.fieldId()));

        List<Tree> trees = field.getTrees();


        existingHarvest.setHarvestDate(harvestRequestDTO.harvestDate());
        existingHarvest.setSeason(harvestRequestDTO.season());


        harvestDetailsRepository.deleteByHarvestId(existingHarvest.getId());


        double updatedTotalQuantity = populateHarvestDetails(existingHarvest, trees);
        existingHarvest.setTotalQuantity(updatedTotalQuantity);


        Harvest updatedHarvest = harvestRepository.save(existingHarvest);

        return harvestMapper.toResponseDTO(updatedHarvest);
    }


    @Override
    public void delete(Long id) {

        Harvest harvest = harvestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Harvest" , id));


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
            harvest.setTotalQuantity(totalQuantity);
        }

        return totalQuantity;
    }



    private void validateNewHarvest(HarvestRequestDTO requestDTO) {
        Field field = fieldRepository.findById(requestDTO.fieldId()).orElseThrow(()->new EntityNotFoundException("Field",requestDTO.fieldId()));

        List<HarvestDetails> existingHarvests = harvestDetailsRepository.findByTree_Field_Farm_IdAndAndHarvest_Season(field.getFarm().getId(),requestDTO.season());

        if (!existingHarvests.isEmpty()) {
            for (HarvestDetails existingHarvest : existingHarvests) {
                if (existingHarvest.getTree().getField().getId().equals(requestDTO.fieldId())) {
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
