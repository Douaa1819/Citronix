package com.citronix.citronix.controller;

import com.citronix.citronix.dto.request.FarmRequestDTO;
import com.citronix.citronix.dto.response.FarmResponseDTO;
import com.citronix.citronix.exception.EntityNotFoundException;
import com.citronix.citronix.service.FarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/farms")
public class FarmController {

    private  final FarmService farmService;

    @GetMapping
    public ResponseEntity<List<FarmResponseDTO>> getAllFarms() {
        List<FarmResponseDTO> farms = farmService.findAll();
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FarmResponseDTO> getFarmById(@PathVariable Long id) {
        FarmResponseDTO farm = farmService.findById(id);
        if (farm == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(farm);
    }

    @PostMapping
    public ResponseEntity<FarmResponseDTO> createFarm(@RequestBody FarmRequestDTO farmRequestDTO) {
        FarmResponseDTO createdFarm = farmService.create(farmRequestDTO);
        return new ResponseEntity<>(createdFarm, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FarmResponseDTO> updateFarm(
            @PathVariable Long id,
            @RequestBody FarmRequestDTO farmRequestDTO) {
        try {
            FarmResponseDTO updatedFarm = farmService.update(id, farmRequestDTO);
            return ResponseEntity.ok(updatedFarm);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFarm(@PathVariable Long id) {
        try {
            farmService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


}