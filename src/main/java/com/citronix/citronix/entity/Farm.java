package com.citronix.citronix.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required ")
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;

    @NotNull(message = "Total area is required ")
    @Positive
    @Column(nullable = false)
    private Double totalArea;

    @NotNull
    @PastOrPresent(message = "The date must be in the past or in the present ")
    @Column(nullable = false)
    private LocalDate creationDate;


    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL)
    private List<Field> fields = new ArrayList<>();

    @Transient
    public Double calculateFieldsTotalArea() {
        if (fields == null || fields.isEmpty()) {
            return 0.0;
        }
        return fields.stream()
                .mapToDouble(Field::getArea)
                .sum();
    }


}