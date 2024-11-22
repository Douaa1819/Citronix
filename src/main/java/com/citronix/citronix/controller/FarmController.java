package com.citronix.citronix.controller;

import com.citronix.citronix.dto.request.FarmRequestDTO;
import com.citronix.citronix.dto.response.FarmResponseDTO;
import com.citronix.citronix.exception.EntityNotFoundException;
import com.citronix.citronix.service.FarmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Validated
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
    public ResponseEntity<FarmResponseDTO> createFarm(@RequestBody @Valid FarmRequestDTO farmRequestDTO) {
        FarmResponseDTO createdFarm = farmService.create(farmRequestDTO);
        return new ResponseEntity<>(createdFarm, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
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
    public ResponseEntity<Map<String, String>> deleteFarm(@PathVariable Long id) {
            farmService.delete(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Farm deleted successfully");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<List<FarmResponseDTO>> searchFarms(@RequestParam String query) {
        try {
            List<FarmResponseDTO> farms = farmService.searchFarms(query);
            return new ResponseEntity<>(farms, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}