package com.citronix.citronix.service.impl;

import com.citronix.citronix.dto.request.FarmRequestDTO;
import com.citronix.citronix.dto.response.FarmResponseDTO;
import com.citronix.citronix.entity.Farm;
import com.citronix.citronix.entity.Field;
import com.citronix.citronix.exception.EntityNotFoundException;
import com.citronix.citronix.mapper.FarmMapper;
import com.citronix.citronix.repository.FarmRepository;
import com.citronix.citronix.repository.FarmSearchRepository;
import com.citronix.citronix.service.FarmService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FarmServiceImpl implements FarmService {

    private final FarmRepository farmRepository;
    private final FarmMapper farmMapper;
    private final FarmSearchRepository searchRepository;



    @Override
    public List<FarmResponseDTO> findAll() {
        return farmRepository.findAll().stream()
                .map(farmMapper::toResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    public FarmResponseDTO findById(Long id) {
        Optional<Farm> farmOptional = farmRepository.findById(id);
        return farmOptional.map(farmMapper::toResponseDTO).orElse(null);
    }

    @Transactional
    @Override
    public FarmResponseDTO create(FarmRequestDTO farmRequestDTO) {
        Farm farm = farmMapper.toEntity(farmRequestDTO);
        Farm savedFarm = farmRepository.save(farm);
        if (farm.getFields() != null) {
            for (Field field : farm.getFields()) {
                field.setFarm(farm);
            }
        }

        return farmMapper.toResponseDTO(savedFarm);
    }

    @Override
    public FarmResponseDTO update(Long id, FarmRequestDTO farmRequestDTO) {
        Optional<Farm> farmOptional = farmRepository.findById(id);
        if (farmOptional.isPresent()) {
            Farm farm = farmOptional.get();
            farmMapper.updateFarmFromDto(farmRequestDTO, farm);
            Farm UpdatedFarm = farmRepository.save(farm);
            return farmMapper.toResponseDTO(UpdatedFarm);
        }else {
            throw new EntityNotFoundException("Farm not found with ID: " + id);
        }
    }

    @Override
    public void delete(Long id) {
        farmRepository.deleteById(id);
    }

    @Override
    public List<FarmResponseDTO> searchFarms(String query) {
        List<Farm> farms = searchRepository.findFarmMultiCriteriaSearch(query);

        return farms.stream()
                .map(farmMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}