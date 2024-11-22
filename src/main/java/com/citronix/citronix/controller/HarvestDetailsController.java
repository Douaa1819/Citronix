package com.citronix.citronix.controller;

import com.citronix.citronix.dto.response.HarvestDetailsResponseDTO;
import com.citronix.citronix.entity.HarvestDetails;
import com.citronix.citronix.service.HarvestDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/harvest-details")
@Validated
public class HarvestDetailsController {

    private final HarvestDetailsService harvestDetailsService;

    @GetMapping
    public List<HarvestDetailsResponseDTO> getAllHarvestDetails() {
        return harvestDetailsService.getAllHarvestDetails();
    }



    @DeleteMapping("/{harvestId}")
    public void deleteHarvestDetailsByHarvestId(@PathVariable Long harvestId) {
        harvestDetailsService.deleteByHarvestId(harvestId);
    }

}

