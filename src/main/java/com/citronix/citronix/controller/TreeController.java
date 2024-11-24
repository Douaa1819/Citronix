package com.citronix.citronix.controller;

import com.citronix.citronix.dto.request.TreeRequestDTO;
import com.citronix.citronix.dto.response.TreeResponseDTO;
import com.citronix.citronix.service.TreeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/trees")
@RequiredArgsConstructor
@Validated
public class TreeController {

    private final TreeService treeService;

    @GetMapping
    public ResponseEntity<Page<TreeResponseDTO>> getAllTrees(
            @RequestParam(defaultValue = "0") Integer pageNum,
        @RequestParam(defaultValue = "10")Integer size){
        Page<TreeResponseDTO> trees = treeService.findAll(pageNum,size);
        return ResponseEntity.ok(trees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreeResponseDTO> getTreeById(@PathVariable Long id) {
        TreeResponseDTO tree = treeService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(tree);
    }

    @PostMapping("/{fieldId}")
    public ResponseEntity<TreeResponseDTO> createTree(
            @PathVariable Long fieldId,
            @Valid @RequestBody TreeRequestDTO treeRequestDTO) {

        TreeRequestDTO updatedTreeRequestDTO = new TreeRequestDTO(treeRequestDTO.plantingDate(), fieldId);

        TreeResponseDTO createdTree = treeService.create(updatedTreeRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTree);
    }




    @PutMapping("/{id}")
    public ResponseEntity<TreeResponseDTO> updateTree(
            @PathVariable Long id,
            @RequestBody @Valid TreeRequestDTO treeRequestDTO) {
        TreeResponseDTO updatedTree = treeService.update(id, treeRequestDTO);
        return ResponseEntity.ok(updatedTree);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTree(@PathVariable Long id) {
        treeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}