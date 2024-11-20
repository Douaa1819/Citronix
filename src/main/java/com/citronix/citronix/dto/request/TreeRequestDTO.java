package com.citronix.citronix.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

public record TreeRequestDTO(
        @NotNull(message = "Planting date is required")
        @PastOrPresent(message = "Planting date must be in the past or present")
        LocalDate plantingDate,

        @NotNull(message = "Field ID is required")
        Long fieldId
) {

    @AssertTrue(message = "Trees can only be planted between March and May")
    public boolean isValidPlantingPeriod() {
        if (plantingDate == null) return false;

        Month month = plantingDate.getMonth();
        return month == Month.MARCH || month == Month.APRIL || month == Month.MAY;
    }


    public boolean isValidTreeAge() {
        long age = ChronoUnit.YEARS.between(plantingDate, LocalDate.now());
        return age <= 20;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDate plantingDate;
        private Long fieldId;

        public Builder plantingDate(LocalDate plantingDate) {
            this.plantingDate = plantingDate;
            return this;
        }

        public Builder fieldId(Long fieldId) {
            this.fieldId = fieldId;
            return this;
        }

        // Perform manual validation before building the record
        public TreeRequestDTO build() {
            if (plantingDate == null) {
                throw new IllegalArgumentException("Planting date is required");
            }

            TreeRequestDTO treeRequestDTO = new TreeRequestDTO(plantingDate, fieldId);

            if (!treeRequestDTO.isValidPlantingPeriod()) {
                throw new IllegalArgumentException("Trees can only be planted between March and May");
            }

            if (!treeRequestDTO.isValidTreeAge()) {
                throw new IllegalArgumentException("Tree age must not exceed 20 years");
            }

            return treeRequestDTO;
        }
    }
}
