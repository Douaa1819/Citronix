package com.citronix.citronix.service.impl;
import com.citronix.citronix.common.exception.EntityConstraintViolationException;
import com.citronix.citronix.dto.request.HarvestRequestDTO;
import com.citronix.citronix.dto.response.HarvestResponseDTO;
import com.citronix.citronix.entity.*;
import com.citronix.citronix.entity.enums.Season;
import com.citronix.citronix.mapper.HarvestMapper;
import com.citronix.citronix.repository.FieldRepository;
import com.citronix.citronix.repository.HarvestDetailsRepository;
import com.citronix.citronix.repository.HarvestRepository;
import com.citronix.citronix.service.FieldService;
import com.citronix.citronix.service.HarvestService;
import jakarta.persistence.EntityNotFoundException;
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
    private final FieldService fieldService;
    private final HarvestMapper harvestMapper;


    @Override
    public Page<HarvestResponseDTO> findAll(int page, int size) {
        return harvestRepository.findAll(PageRequest.of(page,size))
                .map( harvestMapper::toResponseDTO);
    }


    @Override
    public HarvestResponseDTO findById(Long id) {
        Harvest harvest = harvestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Harvest not found with ID: " + id));
        return harvestMapper.toResponseDTO(harvest);
    }


    @Override
    public HarvestResponseDTO create(HarvestRequestDTO harvestRequestDTO) {

        // Validation of constraints
        validateNewHarvest(harvestRequestDTO);

        // Checking dates and season
        isDateMatchingSeason(harvestRequestDTO.harvestDate(), harvestRequestDTO.season());

        Field field = fieldService.findFieldById(harvestRequestDTO.fieldId());



        // Create the harvest and associate with the field
        Harvest harvest = Harvest.builder()
                .harvestDate(harvestRequestDTO.harvestDate())
                .season(harvestRequestDTO.season())
                .farm(field.getFarm())
                .build();

        Harvest savedHarvest = harvestRepository.save(harvest);

        // Add tree details
        double totalQuantity = populateHarvestDetails(savedHarvest, field.getTrees());

        // Update the total quantity harvested
        savedHarvest.setTotalQuantity(totalQuantity);
        harvestRepository.save(savedHarvest);

        return harvestMapper.toResponseDTO(savedHarvest);
    }




    @Override
    public HarvestResponseDTO update(Long id, HarvestRequestDTO harvestRequestDTO) {
        Harvest existingHarvest = harvestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Harvest not found with ID: " + id));


        validateSeasonForUpdate(harvestRequestDTO.season(), id);


        validateHarvestDate(harvestRequestDTO.harvestDate(), harvestRequestDTO.season());


        Field field = fieldService.findFieldById(harvestRequestDTO.fieldId());

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
                .orElseThrow(() -> new EntityNotFoundException("Harvest not found with ID: " + id));


        harvestDetailsRepository.deleteByHarvestId(id);
        harvestRepository.delete(harvest);
    }

    private double populateHarvestDetails(Harvest harvest, List<Tree> trees) {
        double totalQuantity = 0.0;

        for (Tree tree : trees) {
            if (tree.getAge() > 20) continue;

            // Check that this tree has not already been harvested this season
            List<HarvestDetails> existingHarvestDetails = harvestDetailsRepository.findByTreeAndHarvestSeason(tree, harvest.getSeason());
            if (!existingHarvestDetails.isEmpty()) continue;


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
        Field field = fieldService.findFieldById(requestDTO.fieldId());
        Long farmId = field.getFarm().getId();

        // Check if there is already a harvest for this farm and season
        List<HarvestDetails> existingHarvests = harvestDetailsRepository.findByTree_Field_Farm_IdAndAndHarvest_Season(farmId, requestDTO.season());
        if (!existingHarvests.isEmpty()) {
            throw new EntityConstraintViolationException(
                    "Harvest",
                    "A harvest already exists for this farm in the specified season.",
                    requestDTO,
                    "A harvest for the same season and farm already exists."
            );
        }

        // date  Validation
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

            throw new EntityConstraintViolationException(
                    "Harvest",
                    "Harvest date cannot be in the future.",
                    harvestDate,
                    "Invalid harvest date: " + harvestDate
            );
        }

    }

    private void isDateMatchingSeason(LocalDate date, Season season) {
        int month = date.getMonthValue();
        boolean isValidSeason = switch (season) {
            case WINTER -> month == 12 || month == 1 || month == 2;
            case SPRING -> month >= 3 && month <= 5;
            case SUMMER -> month >= 6 && month <= 8;
            case AUTUMN -> month >= 9 && month <= 11;
        };
        if (!isValidSeason) {
            throw new EntityConstraintViolationException("Harvest", "season", season, "does not match the provided date.");
        }
    }


    @Override
    public Harvest findEntityById(Long id) {
        return harvestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }
}





