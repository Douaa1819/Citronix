package com.citronix.citronix.controller;

import com.citronix.citronix.dto.request.SaleRequestDTO;
import com.citronix.citronix.dto.response.SaleResponseDTO;
import com.citronix.citronix.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<SaleResponseDTO> createSale(@RequestBody SaleRequestDTO saleRequestDTO) {
        SaleResponseDTO createdSale = saleService.createSale(saleRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSale);
    }


    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable Long id) {
        SaleResponseDTO sale = saleService.getSaleById(id);
        return ResponseEntity.ok(sale);
    }


    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> getAllSales() {
        List<SaleResponseDTO> sales = saleService.getAllSales();
        return ResponseEntity.ok(sales);
    }


    @PutMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> updateSale(@PathVariable Long id, @RequestBody SaleRequestDTO saleRequestDTO) {
        SaleResponseDTO updatedSale = saleService.updateSale(id, saleRequestDTO);
        return ResponseEntity.ok(updatedSale);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/revenue/{harvestId}")
    public ResponseEntity<Double> calculateRevenue(@PathVariable Long harvestId) {
        Double revenue = saleService.calculateRevenue(harvestId);
        return ResponseEntity.ok(revenue);
    }
}
