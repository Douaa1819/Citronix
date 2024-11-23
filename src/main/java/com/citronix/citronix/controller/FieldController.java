package com.citronix.citronix.controller;

import com.citronix.citronix.dto.request.FieldRequestDTO;
import com.citronix.citronix.dto.response.FieldResponseDTO;
import com.citronix.citronix.service.FieldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/fields")
@RequiredArgsConstructor
@Validated
public class FieldController {

    private final FieldService fieldService;

    @GetMapping
    public ResponseEntity<Page<FieldResponseDTO>> getAllFields(
        @RequestParam(defaultValue = "0") Integer pageNum,
        @RequestParam(defaultValue = "10") Integer pageSize){
        return ResponseEntity.ok(fieldService.findAll(pageNum,pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FieldResponseDTO> getFieldById(@PathVariable Long id) {
        FieldResponseDTO field = fieldService.findById(id);
        return ResponseEntity.ok(field);
    }

    @PostMapping
    public ResponseEntity<FieldResponseDTO> createField(@Valid @RequestBody FieldRequestDTO fieldRequestDTO) {
        FieldResponseDTO createdField = fieldService.create(fieldRequestDTO);
        return new ResponseEntity<>(createdField, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FieldResponseDTO> updateField(
            @PathVariable Long id,
            @Valid @RequestBody FieldRequestDTO fieldRequestDTO) {
        FieldResponseDTO updatedField = fieldService.update(id, fieldRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedField);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteField(@PathVariable Long id) {
        fieldService.delete(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Field deleted successfully");
        return ResponseEntity.ok(response);
    }

}