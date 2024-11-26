package com.citronix.citronix.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Period;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trees")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Planting date is required ")
    @Column(name = "planting_date", nullable = false)
    private LocalDate plantingDate;

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false)
    @NotNull
    private Field field;

    @Transient
    private int age;

    @OneToMany(mappedBy = "tree", cascade = CascadeType.ALL)

    private List<HarvestDetails> harvestDetails = new ArrayList<>();

    public int getAge(){
        return calculateAge();
    }

    private int calculateAge() {
        LocalDate thiscurrentDate = LocalDate.now();
        assert plantingDate != null;

        Period period = Period.between(plantingDate, thiscurrentDate);
        int years = period.getYears();
        int months = period.getMonths();
        return years + (months / 12);
    }

    public Double getProductivity() {
        int age = this.getAge();
        if (age < 3) return 2.5;
        else if (age <= 10) return 12.0;
        else if(age <= 20 ) return 20.0;
        else return 0.0;
    }
}