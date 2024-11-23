package com.citronix.citronix.controller;

import com.citronix.citronix.dto.request.HarvestRequestDTO;
import com.citronix.citronix.dto.response.HarvestResponseDTO;
import com.citronix.citronix.service.HarvestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/harvests")
@RequiredArgsConstructor
@Validated
public class HarvestController {

    private final HarvestService harvestService;

    /**
     * Get all harvests
     *
     * @return List of HarvestResponseDTO
     */

    @GetMapping
    public ResponseEntity<Page<HarvestResponseDTO>> getAllHarvests(
            @RequestParam(defaultValue = "0") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize){
        Page<HarvestResponseDTO> harvests = harvestService.findAll(pageNum,pageSize);
        return ResponseEntity.ok(harvests);
    }

    /**
     * Get a specific harvest by ID
     *
     * @param id Harvest ID
     * @return HarvestResponseDTO
     */

    @GetMapping("/{id}")
    public ResponseEntity<HarvestResponseDTO> getHarvestById(@PathVariable Long id) {
        HarvestResponseDTO harvest = harvestService.findById(id);
        return ResponseEntity.ok(harvest);
    }

    /**
     * Create a new harvest
     *
     * @param harvestRequestDTO Harvest data
     * @return Created HarvestResponseDTO
     */

    @PostMapping
    public ResponseEntity<HarvestResponseDTO> createHarvest(@Valid @RequestBody HarvestRequestDTO harvestRequestDTO) {
        HarvestResponseDTO createdHarvest = harvestService.create(harvestRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHarvest);
    }

    /**
     * Update an existing harvest
     *
     * @param id               Harvest ID to update
     * @param harvestRequestDTO Updated harvest data
     * @return Updated HarvestResponseDTO
     */

    @PutMapping("/{id}")
    public ResponseEntity<HarvestResponseDTO> updateHarvest(
            @PathVariable Long id,
            @Valid @RequestBody HarvestRequestDTO harvestRequestDTO) {
        HarvestResponseDTO updatedHarvest = harvestService.update(id, harvestRequestDTO);
        return ResponseEntity.ok(updatedHarvest);
    }

    /**
     * Delete a harvest by ID
     *
     * @param id Harvest ID
     * @return ResponseEntity with status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHarvest(@PathVariable Long id) {
        harvestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
