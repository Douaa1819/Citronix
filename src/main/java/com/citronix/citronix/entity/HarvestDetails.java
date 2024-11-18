package com.citronix.citronix.entity;

import com.citronix.citronix.entity.embedded.HarvestDetailsId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HarvestDetails {

    @EmbeddedId
    private HarvestDetailsId id;

    @ManyToOne
    @MapsId("harvestId")
    @JoinColumn(name = "harvest_id")
    private Harvest harvest;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("treeId")
    @JoinColumn(name = "tree_id")
    private Tree tree;

    @NotNull(message = "Quantity required")
    @PositiveOrZero
    private Double quantity;

}