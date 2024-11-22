package com.citronix.citronix.entity;


import com.citronix.citronix.entity.enums.Season;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Harvest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Harvest date required ")
    @Column(name = "harvest_date", nullable = false)
    private LocalDate harvestDate;

    @NotNull(message = "Season required")
    @Enumerated(EnumType.STRING)
    private Season season;


    @Transient
    private double totalQuantity;

    @OneToMany(mappedBy = "harvest", cascade = CascadeType.ALL ,orphanRemoval= true)
    @JsonManagedReference
    private List<HarvestDetails> harvestDetails = new ArrayList<>();

}