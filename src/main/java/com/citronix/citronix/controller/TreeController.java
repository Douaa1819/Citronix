package com.citronix.citronix.controller;

import com.citronix.citronix.dto.request.TreeRequestDTO;
import com.citronix.citronix.dto.response.TreeResponseDTO;
import com.citronix.citronix.service.TreeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trees")
@RequiredArgsConstructor
public class TreeController {

    private final TreeService treeService;

    @GetMapping
    public ResponseEntity<List<TreeResponseDTO>> getAllTrees() {
        List<TreeResponseDTO> trees = treeService.findAll();
        return ResponseEntity.ok(trees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TreeResponseDTO> getTreeById(@PathVariable Long id) {
        TreeResponseDTO tree = treeService.findById(id);
        return ResponseEntity.ok(tree);
    }

    @PostMapping
    public ResponseEntity<TreeResponseDTO> createTree(@RequestBody @Valid TreeRequestDTO treeRequestDTO) {
        TreeResponseDTO createdTree = treeService.create(treeRequestDTO);
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