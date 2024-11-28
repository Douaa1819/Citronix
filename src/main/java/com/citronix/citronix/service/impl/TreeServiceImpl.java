package com.citronix.citronix.service.impl;

import com.citronix.citronix.common.exception.EntityConstraintViolationException;
import com.citronix.citronix.dto.request.TreeRequestDTO;
import com.citronix.citronix.dto.response.TreeResponseDTO;
import com.citronix.citronix.entity.Field;
import com.citronix.citronix.entity.Tree;
import com.citronix.citronix.common.exception.EntityNotFoundException;
import com.citronix.citronix.mapper.TreeMapper;
import com.citronix.citronix.repository.FieldRepository;
import com.citronix.citronix.repository.TreeRepository;
import com.citronix.citronix.service.FieldService;
import com.citronix.citronix.service.TreeService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TreeServiceImpl implements TreeService {

    private final TreeRepository treeRepository;
    private final TreeMapper treeMapper;
    private final FieldService fieldService;

    private static final double max_trees_per_hectare = 100.0;
    private static final Month validPlantingPeriod_START_MONTH = Month.MARCH;
    private static final Month validPlantingPeriodEND_MONTH = Month.MAY;

    @Override
    public Page<TreeResponseDTO> findAll(int page, int size) {
        return treeRepository.findAll(PageRequest.of(page,size))
                .map(treeMapper::toResponseDTO);
    }

    @Override
    public TreeResponseDTO findById(Long id) {
        Tree tree = treeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tree ", id));

        return treeMapper.toResponseDTO(tree);
    }

    @Override
    public TreeResponseDTO create(TreeRequestDTO treeRequestDTO) {
        validateTreeRequest(treeRequestDTO);

        Field field = getFieldById(treeRequestDTO.fieldId());
        validateTreeDensity(field);
        Tree tree = treeMapper.toEntity(treeRequestDTO);
        tree.setField(field);

        Tree savedTree = treeRepository.save(tree);
        return treeMapper.toResponseDTO(savedTree);
    }

    @Override
    public TreeResponseDTO update(Long id,TreeRequestDTO treeRequestDTO) {
        validateTreeRequest(treeRequestDTO);

        Tree existingTree = treeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tree " , id));

        Field field = getFieldById(treeRequestDTO.fieldId());
        existingTree.setPlantingDate(treeRequestDTO.plantingDate());
        existingTree.setField(field);

        return treeMapper.toResponseDTO(existingTree);
    }

    @Override
    public void delete(Long id) {
        Tree tree = treeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tree", id));

        treeRepository.delete(tree);
    }


    private Field getFieldById(Long fieldId) {
        return fieldService.findFieldById(fieldId);
    }


    private void validateTreeDensity(Field field) {
        int currentTreeCount = treeRepository.countByField(field);
        double fieldAreaInHectares = field.getArea() / 10000.0;
        double newDensity = (double) (currentTreeCount + 1) / fieldAreaInHectares;

        if (newDensity > max_trees_per_hectare) {
            throw new EntityConstraintViolationException(
                    "Tree",
                    "density",
                    newDensity,
                    String.format("Cannot add more trees. Maximum density is %.0f trees/ha. " +
                                    "Current density with new tree would be %.2f trees/ha.",
                            max_trees_per_hectare, newDensity)
            );
        }
    }


    private void validateTreeRequest (TreeRequestDTO dto ) {

        Month plantingMonth = dto.plantingDate().getMonth();
        if (plantingMonth.getValue() < validPlantingPeriod_START_MONTH.getValue()
                || plantingMonth.getValue() > validPlantingPeriodEND_MONTH.getValue()) {
            throw new EntityConstraintViolationException(
                    "Tree",
                    "planting date",
                    plantingMonth,
                    "Trees can only be planted between March and May"
            );
        }
    }
}
