package com.citronix.citronix.controller;

import com.citronix.citronix.dto.request.FieldRequestDTO;
import com.citronix.citronix.dto.response.FieldResponseDTO;
import com.citronix.citronix.service.FieldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fields")
@RequiredArgsConstructor
public class FieldController {

    private final FieldService fieldService;

    @GetMapping
    public ResponseEntity<List<FieldResponseDTO>> getAllFields() {
        return ResponseEntity.ok(fieldService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FieldResponseDTO> getFieldById(@PathVariable Long id) {
        FieldResponseDTO field = fieldService.findById(id);
        return ResponseEntity.ok(field);
    }

    @PostMapping
    public ResponseEntity<FieldResponseDTO> createField(@RequestBody @Valid FieldRequestDTO fieldRequestDTO) {
        FieldResponseDTO createdField = fieldService.create(fieldRequestDTO);
        return new ResponseEntity<>(createdField, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FieldResponseDTO> updateField(
            @PathVariable Long id,
            @RequestBody FieldRequestDTO fieldRequestDTO) {
        FieldResponseDTO updatedField = fieldService.update(id, fieldRequestDTO);
        return ResponseEntity.ok(updatedField);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteField(@PathVariable Long id) {
        fieldService.delete(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Field deleted successfully");
        return ResponseEntity.ok(response);
    }

}