package com.citronix.citronix.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Unit price is required")
    @Positive
    @Column(nullable = false)
    private Double prixUnitaire;

    @NotNull(message = "Sale date is required")
    @PastOrPresent
    @Column(nullable = false)
    private LocalDate saleDate;

    @NotNull(message = "Quantity is required")
    @Positive
    @Column(nullable = false)
    private Double quantity;

    @ManyToOne
    @JoinColumn(name = "harvest_id", nullable = false)
    private Harvest harvest;

    @NotBlank(message = "Client name is required")
    @Size(min = 2, max = 50, message = "Client name must be between 2 and 50 characters")
    @Column(nullable = false)
    private String clientName;

}