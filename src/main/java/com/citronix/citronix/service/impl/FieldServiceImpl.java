package com.citronix.citronix.service.impl;

import com.citronix.citronix.dto.request.FieldRequestDTO;
import com.citronix.citronix.dto.response.FieldResponseDTO;
import com.citronix.citronix.entity.Farm;
import com.citronix.citronix.entity.Field;
import com.citronix.citronix.mapper.FieldMapper;
import com.citronix.citronix.repository.FarmRepository;
import com.citronix.citronix.repository.FarmSearchRepository;
import com.citronix.citronix.repository.FieldRepository;
import com.citronix.citronix.service.FieldService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;
    private final FarmRepository farmRepository;
    private final FieldMapper fieldMapper;


    @Override
    @Transactional(readOnly = true)
    public List<FieldResponseDTO> findAll() {
        return fieldRepository.findAll()
                .stream()
                .map(fieldMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FieldResponseDTO findById(Long id) {
        return fieldRepository.findById(id)
                .map(fieldMapper::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException("Field not found with id: " + id));
    }

    @Override
    @Transactional
    public FieldResponseDTO create(FieldRequestDTO fieldRequestDTO) {
        // Find the farm
        Farm farm = farmRepository.findById(fieldRequestDTO.farmId())
                .orElseThrow(() -> new EntityNotFoundException("Farm not found with id: " + fieldRequestDTO.farmId()));

        // Validate minimum field size (1,000 m²)
        if (fieldRequestDTO.area() < 1000) {
            throw new IllegalArgumentException("Field area must be at least 1,000 m²");
        }

        // Validate if the new field's area exceeds 50% of the farm's total area
        if (fieldRequestDTO.area() > farm.getTotalArea() * 10_000 * 0.5) { // Convert hectares to m²
            throw new IllegalArgumentException("Field area cannot exceed 50% of the farm's total area");
        }

        // Validate if the new field's area doesn't exceed the farm's remaining area
        double currentFieldsArea = farm.calculateFieldsTotalArea();
        if (currentFieldsArea + fieldRequestDTO.area() > farm.getTotalArea() * 10_000) { // Convert hectares to m²
            throw new IllegalArgumentException("Total field area would exceed the farm's total area");
        }

        // Validate the maximum number of fields (10 per farm)
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
                .orElseThrow(() -> new EntityNotFoundException("Field not found with id: " + id));


        Farm farm = farmRepository.findById(fieldRequestDTO.farmId())
                .orElseThrow(() -> new EntityNotFoundException("Farm not found with id: " + fieldRequestDTO.farmId()));

        // Calculate total
        double totalAreaExcludingCurrent = farm.calculateFieldsTotalArea() - existingField.getArea();

        // Validate minimum field size (1,000 m²)
        if (fieldRequestDTO.area() < 1000) {
            throw new IllegalArgumentException("Field area must be at least 1,000 m²");
        }

        // Validate if the new area exceeds 50% of the farm's total area
        if (fieldRequestDTO.area() > farm.getTotalArea() * 10_000 * 0.5) { // Convert hectares to m²
            throw new IllegalArgumentException("Field area cannot exceed 50% of the farm's total area");
        }

        // Validate if the total area (excluding current field) plus the new area exceeds the farm's total area
        if (totalAreaExcludingCurrent + fieldRequestDTO.area() > farm.getTotalArea() * 10_000) { // Convert hectares to m²
            throw new IllegalArgumentException("Updated field area would exceed farm's total area");
        }

        // Update field
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
                .orElseThrow(() -> new EntityNotFoundException("Field not found with id: " + id));

        fieldRepository.delete(field);
    }


}