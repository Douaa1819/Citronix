package com.citronix.citronix.service.impl;

import com.citronix.citronix.common.exception.EntityNotFoundException;
import com.citronix.citronix.dto.request.FieldRequestDTO;
import com.citronix.citronix.dto.response.FieldResponseDTO;
import com.citronix.citronix.entity.Farm;
import com.citronix.citronix.entity.Field;
import com.citronix.citronix.mapper.FieldMapper;
import com.citronix.citronix.repository.FarmRepository;
import com.citronix.citronix.repository.FieldRepository;
import com.citronix.citronix.service.FieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;
    private final FarmRepository farmRepository;
    private final FieldMapper fieldMapper;


    @Override
    public Page<FieldResponseDTO> findAll(int pageNum, int pageSize) {
        return fieldRepository.findAll(PageRequest.of(pageNum, pageSize))
                .map(fieldMapper::toResponseDTO);
    }

    @Override
    public FieldResponseDTO findById(Long id) {
        return fieldRepository.findById(id)
                .map(fieldMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Field" , id));
    }


    @Override
    @Transactional
    public FieldResponseDTO create(FieldRequestDTO fieldRequestDTO) {

        Farm farm = farmRepository.findById(fieldRequestDTO.farmId())
                .orElseThrow(() -> new EntityNotFoundException("Farm  " , fieldRequestDTO.farmId()));


        if (fieldRequestDTO.area() < 1000) {
            throw new IllegalArgumentException("Field area must be at least 1,000 m²");
        }

        if (fieldRequestDTO.area() > farm.getTotalArea() * 0.5) {
            throw new IllegalArgumentException("Field area cannot exceed 50% of the farm's total area");
        }


        if (farm.getFields().size() >= 10) {
            throw new IllegalArgumentException("A farm cannot have more than 10 fields");
        }

        Field field = fieldMapper.toEntity(fieldRequestDTO);
        field.setFarm(farm);

        Field savedField = fieldRepository.save(field);
        return fieldMapper.toResponseDTO(savedField);
    }



    @Override
    @Transactional
    public FieldResponseDTO update(Long id, FieldRequestDTO fieldRequestDTO) {

        Field existingField = fieldRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Field " , id));


        Farm farm = farmRepository.findById(fieldRequestDTO.farmId())
                .orElseThrow(() -> new EntityNotFoundException("Farm " , fieldRequestDTO.farmId()));



        // Validate minimum field size (1,000 m²)
        if (fieldRequestDTO.area() < 1000) {
            throw new IllegalArgumentException("Field area must be at least 1,000 m²");
        }

        // Validate if the new area exceeds 50% of the farm's total area
        if (fieldRequestDTO.area() > farm.getTotalArea()  * 0.5) {
            throw new IllegalArgumentException("Field area cannot exceed 50% of the farm's total area");
        }


        existingField.setName(fieldRequestDTO.name());
        existingField.setArea(fieldRequestDTO.area());
        existingField.setFarm(farm);


        Field updatedField = fieldRepository.save(existingField);
        return fieldMapper.toResponseDTO(updatedField);
    }


    @Override
    @Transactional
    public void delete(Long id) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Field " ,id));

        fieldRepository.delete(field);
    }


}