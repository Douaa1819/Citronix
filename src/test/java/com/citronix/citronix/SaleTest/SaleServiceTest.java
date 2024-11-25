package com.citronix.citronix.SaleTest;

import com.citronix.citronix.common.exception.EntityNotFoundException;
import com.citronix.citronix.dto.request.SaleRequestDTO;
import com.citronix.citronix.dto.response.SaleResponseDTO;
import com.citronix.citronix.entity.Harvest;
import com.citronix.citronix.entity.HarvestDetails;
import com.citronix.citronix.entity.Sale;
import com.citronix.citronix.mapper.SaleMapper;
import com.citronix.citronix.repository.HarvestRepository;
import com.citronix.citronix.repository.SaleRepository;
import com.citronix.citronix.service.impl.SaleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private HarvestRepository harvestRepository;

    @Mock
    private SaleMapper saleMapper;

    private SaleServiceImpl saleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        saleService = new SaleServiceImpl(saleRepository, harvestRepository, saleMapper);
    }

    @Test
    void testCreateSale_ValidData() {

        SaleRequestDTO requestDTO = new SaleRequestDTO(10.0, LocalDate.of(2024, 11, 1), 5.0, 1L, "ClientName");
        Harvest harvest = Harvest.builder()
                .id(1L)
                .harvestDetails(List.of(new HarvestDetails(null, null,null, 15.0)))
                .totalQuantity(15.0)
                .build();
        Sale sale = Sale.builder()
                .id(1L)
                .quantity(5.0)
                .prixUnitaire(10.0)
                .build();


        when(harvestRepository.findById(1L)).thenReturn(Optional.of(harvest));
        when(saleRepository.save(any(Sale.class))).thenReturn(sale);
        when(saleMapper.toEntity(any(SaleRequestDTO.class))).thenReturn(sale);
        when(saleMapper.toDTO(any(Sale.class))).thenReturn(new SaleResponseDTO(1L, 5.0, LocalDate.of(2024, 11, 1), 10.0, 50.0, "ClientName", LocalDate.of(2024, 11, 1)));

        SaleResponseDTO response = saleService.create(requestDTO);

        // Validate results
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(5.0, response.quantity());
        assertEquals(50.0, response.revenue());
        verify(harvestRepository, times(1)).save(harvest);
        verify(saleRepository, times(1)).save(sale);
    }

    @Test
    void testCreateSale_InsufficientQuantity() {
        SaleRequestDTO requestDTO = new SaleRequestDTO(10.0, LocalDate.of(2024, 11, 1), 20.0, 1L, "ClientName");
        Harvest harvest = Harvest.builder()
                .id(1L)
                .harvestDetails(List.of(new HarvestDetails( null,null,null,15.0)))
                .totalQuantity(15.0)
                .build();

        when(harvestRepository.findById(1L)).thenReturn(Optional.of(harvest));


        assertThrows(IllegalArgumentException.class, () -> saleService.create(requestDTO));
    }


    @Test
    void testCalculateRevenue_ValidData() {
        // Prepare test data (two sales)
        Sale sale1 = Sale.builder()
                .quantity(5.0)
                .prixUnitaire(10.0)
                .build();

        Sale sale2 = Sale.builder()
                .quantity(3.0)
                .prixUnitaire(15.0)
                .build();


        when(saleRepository.findByHarvestId(1L)).thenReturn(List.of(sale1, sale2));


        Double revenue = saleService.calculateRevenue(1L);


        assertNotNull(revenue);
        assertEquals(95.0, revenue); // (5 * 10) + (3 * 15) = 50 + 45 = 95

        verify(saleRepository, times(1)).findByHarvestId(1L);
    }

    @Test
    void testCalculateRevenue_NoSales() {

        when(saleRepository.findByHarvestId(1L)).thenReturn(List.of());


        Double revenue = saleService.calculateRevenue(1L);

        assertNotNull(revenue);
        assertEquals(0.0, revenue);

        verify(saleRepository, times(1)).findByHarvestId(1L);
    }

    @Test
    void testCalculateRevenue_SingleSale() {

        Sale sale = Sale.builder()
                .quantity(4.0)
                .prixUnitaire(20.0)
                .build();

        when(saleRepository.findByHarvestId(1L)).thenReturn(List.of(sale));

        Double revenue = saleService.calculateRevenue(1L);


        assertNotNull(revenue);
        assertEquals(80.0, revenue); // 4 * 20 = 80


        verify(saleRepository, times(1)).findByHarvestId(1L);
    }


}
