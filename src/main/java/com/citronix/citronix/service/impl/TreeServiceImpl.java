package com.citronix.citronix.service.impl;

import com.citronix.citronix.dto.request.TreeRequestDTO;
import com.citronix.citronix.dto.response.TreeResponseDTO;
import com.citronix.citronix.entity.Field;
import com.citronix.citronix.entity.Tree;
import com.citronix.citronix.common.exception.EntityNotFoundException;
import com.citronix.citronix.mapper.TreeMapper;
import com.citronix.citronix.repository.FieldRepository;
import com.citronix.citronix.repository.TreeRepository;
import com.citronix.citronix.service.TreeService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TreeServiceImpl implements TreeService {

    private final TreeRepository treeRepository;
    private final TreeMapper treeMapper;
    private final FieldRepository fieldRepository;

    @Override
    public Page<TreeResponseDTO> findAll(int page, int size) {
        return treeRepository.findAll(PageRequest.of(page,size))
                .map(treeMapper::toResponseDTO);
    }

    @Override
    public TreeResponseDTO findById(Long id) {
        Tree tree = treeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tree ", id));

        return treeMapper.toResponseDTO(tree).calculateAgeAndProductivity();
    }

    @Override
    public TreeResponseDTO create(@Valid TreeRequestDTO treeRequestDTO) {
        validateTreeRequest(treeRequestDTO);

        Field field = getFieldById(treeRequestDTO.fieldId());
        Tree tree = treeMapper.toEntity(treeRequestDTO);
        tree.setField(field);

        Tree savedTree = treeRepository.save(tree);
        return treeMapper.toResponseDTO(savedTree).calculateAgeAndProductivity();
    }

    @Override
    public TreeResponseDTO update(Long id, @Valid TreeRequestDTO treeRequestDTO) {
        validateTreeRequest(treeRequestDTO);

        Tree existingTree = treeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tree " , id));

        Field field = getFieldById(treeRequestDTO.fieldId());
        existingTree.setPlantingDate(treeRequestDTO.plantingDate());
        existingTree.setField(field);

        Tree updatedTree = treeRepository.save(existingTree);
        return treeMapper.toResponseDTO(updatedTree).calculateAgeAndProductivity();
    }

    @Override
    public void delete(Long id) {
        Tree tree = treeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tree", id));

        treeRepository.delete(tree);
    }

    private void validateTreeRequest(TreeRequestDTO treeRequestDTO) {
        if (!treeRequestDTO.isValidPlantingPeriod()) {
            throw new IllegalArgumentException("Trees can only be planted between March and May");
        }

        if (!treeRequestDTO.isValidTreeAge()) {
            throw new IllegalArgumentException("Tree age must not exceed 20 years");
        }

    }

    private Field getFieldById(Long fieldId) {
        return fieldRepository.findById(fieldId)
                .orElseThrow(() -> new EntityNotFoundException("Field " , fieldId));
    }
}
