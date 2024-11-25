package com.citronix.citronix.HarvestTest;

import com.citronix.citronix.common.exception.EntityConstraintViolationException;
import com.citronix.citronix.common.exception.EntityNotFoundException;
import com.citronix.citronix.dto.request.HarvestRequestDTO;
import com.citronix.citronix.dto.response.HarvestResponseDTO;
import com.citronix.citronix.entity.*;
import com.citronix.citronix.entity.enums.Season;
import com.citronix.citronix.mapper.HarvestMapper;
import com.citronix.citronix.repository.FieldRepository;
import com.citronix.citronix.repository.HarvestDetailsRepository;
import com.citronix.citronix.repository.HarvestRepository;
import com.citronix.citronix.service.impl.HarvestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HarvestServiceTest {

    @Mock
    private HarvestRepository harvestRepository;

    @Mock
    private HarvestDetailsRepository harvestDetailsRepository;

    @Mock
    private FieldRepository fieldRepository;

    @Mock
    private HarvestMapper harvestMapper;

    private HarvestServiceImpl harvestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        harvestService = new HarvestServiceImpl(harvestRepository, harvestDetailsRepository, fieldRepository, harvestMapper);
    }



    @Test
    void testUpdateHarvest_ValidData() {

        Long harvestId = 1L;
        Harvest existingHarvest = Harvest.builder()
                .id(harvestId)
                .harvestDate(LocalDate.of(2024, 10, 1))
                .season(Season.SUMMER)
                .totalQuantity(5.5)
                .build();

        HarvestRequestDTO requestDTO = new HarvestRequestDTO(
                LocalDate.of(2024, 11, 1),
                Season.AUTUMN,
                3L
        );

        Field field = new Field();
        field.setId(3L);
        field.setFarm(new Farm());
        field.setTrees(List.of(new Tree(1L, LocalDate.of(2020, 5, 10), field, 4, null)));

        when(harvestRepository.findById(harvestId)).thenReturn(Optional.of(existingHarvest));
        when(fieldRepository.findById(3L)).thenReturn(Optional.of(field));
        when(harvestRepository.save(any(Harvest.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(harvestMapper.toResponseDTO(any(Harvest.class))).thenReturn(new HarvestResponseDTO(1L, LocalDate.of(2024, 11, 1), Season.AUTUMN));


        HarvestResponseDTO response = harvestService.update(harvestId, requestDTO);


        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(Season.AUTUMN, response.season());
        verify(harvestDetailsRepository, times(1)).deleteByHarvestId(harvestId); // Détails précédents supprimés
        verify(harvestDetailsRepository, times(1)).save(any(HarvestDetails.class)); // Détails mis à jour
    }


    @Test
    void testDeleteHarvest_ExistingHarvest() {

        Long harvestId = 1L;
        Harvest existingHarvest = Harvest.builder()
                .id(harvestId)
                .build();

        when(harvestRepository.findById(harvestId)).thenReturn(Optional.of(existingHarvest));


        harvestService.delete(harvestId);


        verify(harvestRepository, times(1)).delete(existingHarvest);
        verify(harvestDetailsRepository, times(1)).deleteByHarvestId(harvestId);
    }



}

