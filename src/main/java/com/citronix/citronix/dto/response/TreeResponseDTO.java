package com.citronix.citronix.dto.response;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record TreeResponseDTO(
        Long id,
        LocalDate plantingDate,
        Integer age,
        Double expectedYieldPerSeason
) {

    public TreeResponseDTO calculateAgeAndProductivity() {
        if (plantingDate != null) {
            long calculatedAge = ChronoUnit.YEARS.between(plantingDate, LocalDate.now());
            Integer calculatedAgeInt = (int) calculatedAge; // Calcul de l'âge
            Double calculatedProductivity = calculateProductivity(calculatedAgeInt);


            return new TreeResponseDTO(id, plantingDate, calculatedAgeInt, calculatedProductivity);
        }
        return this;
    }

    // Méthode pour calculer la productivité selon l'âge
    private Double calculateProductivity(int age) {
        if (age < 3) return 2.5;
        else if (age <= 10) return 12.0;
        else if (age <= 20) return 20.0;
        else return 0.0;
    }

    // Builder pour TreeResponseDTO
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private LocalDate plantingDate;
        private Integer age;
        private String productivityCategory;
        private Double expectedYieldPerSeason;


        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder plantingDate(LocalDate plantingDate) {
            this.plantingDate = plantingDate;
            return this;
        }

        public Builder age(Integer age) {
            this.age = age;
            return this;
        }

        public TreeResponseDTO build() {
            if (plantingDate != null) {
                long calculatedAge = ChronoUnit.YEARS.between(plantingDate, LocalDate.now());
                this.age = (int) calculatedAge;
                this.expectedYieldPerSeason = calculateProductivity((int) calculatedAge);
            }
            return new TreeResponseDTO(id, plantingDate, age, expectedYieldPerSeason);
        }

        private Double calculateProductivity(int age) {
            if (age < 3) return 2.5;
            else if (age <= 10) return 12.0;
            else if (age <= 20) return 20.0;
            else return 0.0;
        }
    }
}
