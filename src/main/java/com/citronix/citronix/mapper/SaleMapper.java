package com.citronix.citronix.mapper;

import com.citronix.citronix.dto.request.SaleRequestDTO;
import com.citronix.citronix.dto.response.SaleResponseDTO;
import com.citronix.citronix.entity.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    @Mapping(target = "harvest", ignore = true)
    @Mapping(target = "id", ignore = true)
    Sale toEntity(SaleRequestDTO dto);

    @Mapping(target = "revenue", expression = "java(calculateRevenue(sale))")
    @Mapping(target = "harvestId", source = "harvest.id")
    @Mapping(target = "harvestDate", source = "harvest.harvestDate")
    @Mapping(target = "season", source = "harvest.season")
    SaleResponseDTO toDTO(Sale sale);

    @Named("calculateRevenue")
    default Double calculateRevenue(Sale sale) {
        return sale.getQuantity() * sale.getPrixUnitaire();
    }
}