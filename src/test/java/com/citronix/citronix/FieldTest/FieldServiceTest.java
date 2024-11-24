package com.citronix.citronix.FieldTest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.citronix.citronix.common.exception.EntityConstraintViolationException;
import com.citronix.citronix.common.exception.EntityNotFoundException;
import com.citronix.citronix.dto.request.FieldRequestDTO;
import com.citronix.citronix.dto.response.FieldResponseDTO;
import com.citronix.citronix.entity.Farm;
import com.citronix.citronix.entity.Field;
import com.citronix.citronix.mapper.FieldMapper;
import com.citronix.citronix.repository.FarmRepository;
import com.citronix.citronix.repository.FieldRepository;
import com.citronix.citronix.service.impl.FieldServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class FieldServiceImplTest {

    @Mock
    private FieldRepository fieldRepository;

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private FieldMapper fieldMapper;

    @InjectMocks
    private FieldServiceImpl fieldService;

    private Farm farm;
    private FieldRequestDTO validFieldRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);


        farm = new Farm();
        farm.setId(1L);
        farm.setTotalArea(10.0);

        validFieldRequestDTO = new FieldRequestDTO("Field 1", 5000.0, 1L); // 5000 m²
    }

    @Test
    void testCreateField_Success() {
        Field field = new Field();
        field.setFarm(farm);
        field.setArea(validFieldRequestDTO.area());
        field.setName(validFieldRequestDTO.name());

        // Initialisation de la ferme et des champs
        farm.setTotalArea(10.0);
        farm.setFields(new ArrayList<>());

        // Mock des comportements
        when(farmRepository.findById(validFieldRequestDTO.farmId())).thenReturn(Optional.of(farm));
        when(fieldMapper.toEntity(validFieldRequestDTO)).thenReturn(field);
        when(fieldRepository.save(field)).thenReturn(field);
        when(fieldMapper.toResponseDTO(field)).thenReturn(new FieldResponseDTO(
                1L, "Field 1", 5000.0, new ArrayList<>(), null
        ));

        // Appel de la méthode
        FieldResponseDTO response = fieldService.create(validFieldRequestDTO);

        // Assertions sur la réponse
        assertNotNull(response);
        assertEquals("Field 1", response.name());
        assertEquals(5000.0, response.area(), 0.001);

        // Validation de la surface totale des champs
        double totalFieldSurface = farm.getFields().stream().mapToDouble(Field::getArea).sum();
        double farmTotalAreaInSquareMeters = farm.getTotalArea() * 10000;
        assertTrue(totalFieldSurface <= farmTotalAreaInSquareMeters,
                "La surface totale des champs dépasse celle de la ferme.");
    }



    @Test
    void testCreateField_InvalidFarm() {
        when(farmRepository.findById(validFieldRequestDTO.farmId())).thenReturn(java.util.Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> fieldService.create(validFieldRequestDTO));
        assertEquals("Farm   with id 1 not found", exception.getMessage());
    }

    @Test
    void testCreateField_SmallArea() {
        FieldRequestDTO smallFieldRequestDTO = new FieldRequestDTO("Small Field", 500.0, 1L);

        when(farmRepository.findById(smallFieldRequestDTO.farmId())).thenReturn(java.util.Optional.of(farm));

        EntityConstraintViolationException exception = assertThrows(EntityConstraintViolationException.class, () -> fieldService.create(smallFieldRequestDTO));
        assertEquals("The 'area' attribute of the 'Field' entity has an invalid value: '500.0'. Field area must be at least 1,000 m²", exception.getMessage());
    }

    @Test
    void testCreateField_ExceedsFarmArea() {
        FieldRequestDTO largeFieldRequestDTO = new FieldRequestDTO("Large Field", 60000.0, 1L);

        when(farmRepository.findById(largeFieldRequestDTO.farmId())).thenReturn(java.util.Optional.of(farm));

        EntityConstraintViolationException exception = assertThrows(EntityConstraintViolationException.class, () -> fieldService.create(largeFieldRequestDTO));
        assertEquals("The 'area' attribute of the 'Field' entity has an invalid value: '60000.0'. Field area cannot exceed 50% of the farm's total area", exception.getMessage());
    }

    @Test
    void testCreateField_MaxFieldsExceeded() {
        farm.setFields(List.of(new Field(), new Field(), new Field(), new Field(), new Field(),
                new Field(), new Field(), new Field(), new Field(), new Field()));

        when(farmRepository.findById(validFieldRequestDTO.farmId())).thenReturn(java.util.Optional.of(farm));

        EntityConstraintViolationException exception = assertThrows(EntityConstraintViolationException.class, () -> fieldService.create(validFieldRequestDTO));
        assertEquals("The 'fields' attribute of the 'Farm' entity has an invalid value: '10'. A farm cannot have more than 10 fields", exception.getMessage());
    }


    @Test
    void testCreateField_ExceedsFieldAreaLimit() {
        farm.setTotalArea(3.0);
        farm.setFields(new ArrayList<>());

        FieldRequestDTO largeFieldRequestDTO = new FieldRequestDTO("Excessive Field", 16000.0, 1L);

        Field excessiveField = new Field();
        excessiveField.setName("Excessive Field");
        excessiveField.setArea(16000.0);
        excessiveField.setFarm(farm);

        when(farmRepository.findById(anyLong())).thenReturn(Optional.of(farm));
        when(fieldMapper.toEntity(largeFieldRequestDTO)).thenReturn(excessiveField);

        EntityConstraintViolationException exception = assertThrows(EntityConstraintViolationException.class,
                () -> fieldService.create(largeFieldRequestDTO));

        assertEquals("The 'area' attribute of the 'Field' entity has an invalid value: '16000.0'. Field area cannot exceed 50% of the farm's total area",
                exception.getMessage());
    }


    @Test
    void testCreateField_ExceedsFarmSurface() {
        farm.setTotalArea(3.0); // 3 hectares
        farm.setFields(new ArrayList<>());


        FieldRequestDTO validFieldRequestDTO = new FieldRequestDTO("Valid Field", 15000.0, 1L);
        Field validField = new Field();
        validField.setName("Valid Field");
        validField.setArea(15000.0);
        validField.setFarm(farm);
        farm.getFields().add(validField);


        FieldRequestDTO extraFieldRequestDTO = new FieldRequestDTO("Extra Field", 16000.0, 1L);
        Field extraField = new Field();
        extraField.setName("Extra Field");
        extraField.setArea(16000.0);
        extraField.setFarm(farm);


        when(farmRepository.findById(anyLong())).thenReturn(Optional.of(farm));
        when(fieldMapper.toEntity(extraFieldRequestDTO)).thenReturn(extraField);


        EntityConstraintViolationException exception = assertThrows(EntityConstraintViolationException.class,
                () -> fieldService.create(extraFieldRequestDTO));


        assertEquals("The 'area' attribute of the 'Field' entity has an invalid value: '16000.0'. Field area cannot exceed 50% of the farm's total area",
                exception.getMessage());
    }






}
