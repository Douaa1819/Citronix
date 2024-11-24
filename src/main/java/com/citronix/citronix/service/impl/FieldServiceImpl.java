package com.citronix.citronix.service.impl;

import com.citronix.citronix.common.exception.EntityConstraintViolationException;
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
                .orElseThrow(() -> new EntityNotFoundException("Field", id));
    }


    @Override
    public FieldResponseDTO create(FieldRequestDTO fieldRequestDTO) {

        Farm farm = farmRepository.findById(fieldRequestDTO.farmId())
                .orElseThrow(() -> new EntityNotFoundException("Farm  ", fieldRequestDTO.farmId()));


        // Validate minimum field size (1,000 m²)
        if (fieldRequestDTO.area() < 1000) {
            throw new EntityConstraintViolationException("Field", "area", fieldRequestDTO.area(), "Field area must be at least 1,000 m²");
        }

        // Validate if the new area exceeds 50% of the farm's total area
        if (fieldRequestDTO.area() > farm.getTotalArea() * 0.5) {
            throw new EntityConstraintViolationException("Field", "area", fieldRequestDTO.area(), "Field area cannot exceed 50% of the farm's total area");
        }

        if (farm.getFields().size() >= 10) {
            throw new EntityConstraintViolationException("Farm", "fields", farm.getFields().size(), "A farm cannot have more than 10 fields");
        }


        double newFieldSurface = fieldRequestDTO.area();
        validateTotalFarmSurface(farm, newFieldSurface, null);

        Field field = fieldMapper.toEntity(fieldRequestDTO);
        field.setFarm(farm);

        Field savedField = fieldRepository.save(field);
        return fieldMapper.toResponseDTO(savedField);
    }
    @Override
    public FieldResponseDTO update(Long id, FieldRequestDTO fieldRequestDTO) {

        Field existingField = fieldRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Field", id));


        Farm farm = farmRepository.findById(fieldRequestDTO.farmId())
                .orElseThrow(() -> new EntityNotFoundException("Farm", fieldRequestDTO.farmId()));

        if (fieldRequestDTO.area() < 1000) {
            throw new EntityConstraintViolationException("Field", "area", fieldRequestDTO.area(), "Field area must be at least 1,000 m²");
        }

        if (fieldRequestDTO.area() > farm.getTotalArea() * 0.5) {
            throw new EntityConstraintViolationException("Field", "area", fieldRequestDTO.area(), "Field area cannot exceed 50% of the farm's total area");
        }

        double newFieldSurface = fieldRequestDTO.area();
        validateTotalFarmSurface(farm, newFieldSurface, existingField.getId());

        updateFieldProperties(existingField, farm, fieldRequestDTO);

        Field updatedField = fieldRepository.save(existingField);
        return fieldMapper.toResponseDTO(updatedField);
    }

    private void updateFieldProperties(Field field, Farm farm, FieldRequestDTO dto) {
        if (!field.getFarm().equals(farm)) {
            field.setFarm(farm);
        }
        field.setName(dto.name());
        field.setArea(dto.area());
    }

    @Override
    public void delete(Long id) {
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Field ", id));

        fieldRepository.delete(field);
    }

    private void validateTotalFarmSurface(Farm farm, double newFieldSurface, Long excludeFieldId) {

        double totalFieldSurface = farm.getFields().stream()
                .filter(field -> excludeFieldId == null || !field.getId().equals(excludeFieldId))
                .mapToDouble(Field::getArea)
                .sum() + newFieldSurface;

        if (farm.getTotalArea() < totalFieldSurface) {
            throw new EntityConstraintViolationException("Farm", "Surface", totalFieldSurface,
                    String.format("The total surface of all fields cannot exceed the farm's surface .",
                            farm.getTotalArea()));
        }
    }

    @Override
    public Field findFieldById( Long id ) {
        return fieldRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("field", id));

    }
}