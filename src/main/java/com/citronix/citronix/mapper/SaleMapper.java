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

    @Mapping(expression = "java(sale.getQuantity() * sale.getPrixUnitaire())", target = "revenue")
    @Mapping(source = "sale.quantity", target = "quantity")
    @Mapping(source = "sale.saleDate", target = "saleDate")
    @Mapping(source = "sale.prixUnitaire", target = "prixUnitaire")
    @Mapping(source = "sale.clientName", target = "clientName")
    @Mapping(source = "sale.harvest.harvestDate", target = "harvestDate")
    SaleResponseDTO toDTO(Sale sale);

    @Named("calculateRevenue")
    default Double calculateRevenue(Sale sale) {
        return sale.getQuantity() * sale.getPrixUnitaire();
    }
}