package com.citronix.citronix.FarmTest;

import com.citronix.citronix.common.exception.EntityConstraintViolationException;
import com.citronix.citronix.common.exception.EntityNotFoundException;
import com.citronix.citronix.dto.request.FarmRequestDTO;
import com.citronix.citronix.dto.response.FarmResponseDTO;
import com.citronix.citronix.entity.Farm;
import com.citronix.citronix.mapper.FarmMapper;
import com.citronix.citronix.repository.FarmRepository;
import com.citronix.citronix.repository.FarmSearchRepository;
import com.citronix.citronix.service.impl.FarmServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FarmServiceImplTest {

    @InjectMocks
    private FarmServiceImpl farmService;

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private FarmMapper farmMapper;

    @Mock
    private FarmSearchRepository searchRepository;

    public FarmServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }


    @Nested
    @DisplayName("Tests for create")
    class CreateTests {

        @Test
        @DisplayName("Should create a farm successfully")
        void shouldCreateFarmSuccessfully() {
            FarmRequestDTO requestDTO = new FarmRequestDTO("Farm Douaa", "Location Douaa", 3.0, LocalDate.now());
            Farm farm = new Farm(null, "Farm Douaa", "Location Douaa", 3.0, LocalDate.now(), List.of(), List.of());
            Farm savedFarm = new Farm(1L, "Farm Douaa", "Location Douaa", 3.0, LocalDate.now(), List.of(), List.of());
            FarmResponseDTO responseDTO = new FarmResponseDTO(1L, "Farm Douaa", "Location Douaa", 3.0, LocalDate.now(), List.of());

            when(farmMapper.toEntity(requestDTO)).thenReturn(farm);
            when(farmRepository.save(farm)).thenReturn(savedFarm);
            when(farmMapper.toResponseDTO(savedFarm)).thenReturn(responseDTO);

            FarmResponseDTO result = farmService.create(requestDTO);

            assertNotNull(result);
            assertEquals(responseDTO, result);
            verify(farmRepository, times(1)).save(farm);
        }

        @Test
        @DisplayName("Should throw exception when total area is below 2000")
        void shouldThrowExceptionWhenTotalAreaBelow2000() {
            FarmRequestDTO requestDTO = new FarmRequestDTO("Farm B", "Location B", 1.5, LocalDate.now());

            assertThrows(EntityConstraintViolationException.class, () -> farmService.create(requestDTO));
        }
    }

    @Nested
    @DisplayName("Tests for update")
    class UpdateTests {

        @Test
        @DisplayName("Should update farm successfully")
        void shouldUpdateFarmSuccessfully() {
            Long id = 1L;
            FarmRequestDTO requestDTO = new FarmRequestDTO("Updated Farm", "Updated Location", 4.0, LocalDate.now());
            Farm existingFarm = new Farm(id, "Old Farm", "Old Location", 3.0, LocalDate.now(), List.of(), List.of());
            Farm updatedFarm = new Farm(id, "Updated Farm", "Updated Location", 4.0, LocalDate.now(), List.of(), List.of());
            FarmResponseDTO responseDTO = new FarmResponseDTO(id, "Updated Farm", "Updated Location", 4.0, LocalDate.now(), List.of());

            when(farmRepository.findById(id)).thenReturn(Optional.of(existingFarm));
            when(farmRepository.save(existingFarm)).thenReturn(updatedFarm);
            when(farmMapper.toResponseDTO(updatedFarm)).thenReturn(responseDTO);

            FarmResponseDTO result = farmService.update(id, requestDTO);

            assertNotNull(result);
            assertEquals(responseDTO, result);
        }

        @Test
        @DisplayName("Should throw exception when farm is not found")
        void shouldThrowExceptionWhenFarmNotFound() {
            Long id = 1L;
            FarmRequestDTO requestDTO = new FarmRequestDTO("Updated Farm", "Updated Location", 4.0, LocalDate.now());

            when(farmRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> farmService.update(id, requestDTO));
        }
    }

    @Nested
    @DisplayName("Tests for delete")
    class DeleteTests {

        @Test
        @DisplayName("Should delete farm successfully")
        void shouldDeleteFarmSuccessfully() {
            Long id = 1L;
            Farm farm = new Farm(id, "Farm A", "Location A", 3.0, LocalDate.now(), List.of(), List.of());

            when(farmRepository.findById(id)).thenReturn(Optional.of(farm));
            doNothing().when(farmRepository).delete(farm);

            farmService.delete(id);

            verify(farmRepository, times(1)).delete(farm);
        }

        @Test
        @DisplayName("Should throw exception when farm is not found")
        void shouldThrowExceptionWhenFarmNotFound() {
            Long id = 1L;

            when(farmRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> farmService.delete(id));
        }
    }

    @Nested
    @DisplayName("Tests for searchFarms")
    class SearchTests {

        @Test
        @DisplayName("Should return list of farms matching query")
        void shouldReturnListOfMatchingFarms() {
            String query = "Farm A";
            Farm farm = new Farm(1L, "Farm A", "Location A", 3.0, LocalDate.now(), List.of(), List.of());
            FarmResponseDTO responseDTO = new FarmResponseDTO(1L, "Farm A", "Location A", 3.0, LocalDate.now(), List.of());

            when(searchRepository.findFarmMultiCriteriaSearch(query)).thenReturn(List.of(farm));
            when(farmMapper.toResponseDTO(farm)).thenReturn(responseDTO);

            List<FarmResponseDTO> result = farmService.searchFarms(query);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(responseDTO, result.get(0));
        }
    }
}
