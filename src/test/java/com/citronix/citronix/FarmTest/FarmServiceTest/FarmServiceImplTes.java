package com.citronix.citronix.FarmTest.FarmServiceTest;

import com.citronix.citronix.dto.request.FarmRequestDTO;
import com.citronix.citronix.dto.response.FarmResponseDTO;
import com.citronix.citronix.entity.Farm;
import com.citronix.citronix.exception.EntityNotFoundException;
import com.citronix.citronix.mapper.FarmMapper;
import com.citronix.citronix.repository.FarmRepository;
import com.citronix.citronix.repository.FarmSearchRepository;
import com.citronix.citronix.service.impl.FarmServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class FarmServiceImplTest {

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private FarmMapper farmMapper;

    @Mock
    private FarmSearchRepository searchRepository;

    @InjectMocks
    private FarmServiceImpl farmService;

    private Farm farm;
    private FarmRequestDTO farmRequestDTO;
    private FarmResponseDTO farmResponseDTO;

    @BeforeEach
    void setUp() {

        farm = Farm.builder()
                .id(1L)
                .name("Farm Douaa")
                .location("Location A")
                .totalArea(100.0)
                .creationDate(LocalDate.now())
                .fields(new ArrayList<>())
                .build();


        farmRequestDTO = new FarmRequestDTO("Farm A", "Location A", 100.0, LocalDate.now());

        // Création de FarmResponseDTO attendu
        farmResponseDTO = new FarmResponseDTO(1L, "Farm A", "Location A", 100.0, LocalDate.now(), new ArrayList<>());
    }

    /**
     * Test pour vérifier que la méthode findAll() retourne correctement une liste de FarmResponseDTO.
     */
    @Test
    void findAll_ShouldReturnFarmResponseDTOList() {

        when(farmRepository.findAll()).thenReturn(Arrays.asList(farm));
        when(farmMapper.toResponseDTO(farm)).thenReturn(farmResponseDTO);


        List<FarmResponseDTO> result = farmService.findAll();

        assertNotNull(result, "La liste ne doit pas être nulle.");
        assertEquals(1, result.size(), "La taille de la liste devrait être 1.");
        assertEquals(farmResponseDTO, result.get(0), "Les résultats devraient correspondre.");
        verify(farmRepository, times(1)).findAll();
        verify(farmMapper, times(1)).toResponseDTO(farm);
    }

    /**
     * Test pour vérifier que la méthode findById() retourne une ferme existante.
     */
    @Test
    void findById_ShouldReturnFarmResponseDTO_WhenFarmExists() {

        when(farmRepository.findById(1L)).thenReturn(Optional.of(farm));
        when(farmMapper.toResponseDTO(farm)).thenReturn(farmResponseDTO);


        FarmResponseDTO result = farmService.findById(1L);


        assertNotNull(result, "La réponse ne doit pas être nulle.");
        assertEquals(farmResponseDTO, result, "Les objets doivent être égaux.");
        verify(farmRepository, times(1)).findById(1L);
    }

    /**
     * Test pour vérifier que la méthode create() retourne la bonne réponse après création d'une ferme.
     */
    @Test
    void create_ShouldReturnFarmResponseDTO_WhenSuccessful() {

        when(farmMapper.toEntity(farmRequestDTO)).thenReturn(farm);
        when(farmRepository.save(farm)).thenReturn(farm);
        when(farmMapper.toResponseDTO(farm)).thenReturn(farmResponseDTO);


        FarmResponseDTO result = farmService.create(farmRequestDTO);


        assertNotNull(result, "La réponse ne doit pas être nulle.");
        assertEquals(farmResponseDTO, result, "Les objets doivent être égaux.");
        verify(farmRepository, times(1)).save(farm);
    }

    /**
     * Test pour vérifier que la méthode update() lance une exception si la ferme n'existe pas.
     */
    @Test
    void update_ShouldThrowEntityNotFoundException_WhenFarmDoesNotExist() {

        when(farmRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert : vérifier que l'exception est lancée
        assertThrows(EntityNotFoundException.class, () -> farmService.update(1L, farmRequestDTO));
        verify(farmRepository, times(1)).findById(1L);
    }

    /**
     * Test pour vérifier que la méthode update() retourne le farm mis à jour lorsqu'elle est réussie.
     */
    @Test
    void update_ShouldReturnFarmResponseDTO_WhenFarmExists() {

        // Suppose that farm with ID 1 exists
        when(farmRepository.findById(1L)).thenReturn(Optional.of(farm));
        when(farmMapper.toEntity(farmRequestDTO)).thenReturn(farm);
        when(farmRepository.save(farm)).thenReturn(farm);
        when(farmMapper.toResponseDTO(farm)).thenReturn(farmResponseDTO);

        FarmResponseDTO result = farmService.update(1L, farmRequestDTO);

        // Assert: verifying the response is as expected
        assertNotNull(result, "La réponse ne doit pas être nulle.");
        assertEquals(farmResponseDTO, result, "Les objets doivent être égaux.");
        verify(farmRepository, times(1)).save(farm);
    }

    /**
     * Test pour vérifier que la méthode delete() supprime la ferme si elle existe.
     */
    @Test
    void delete_ShouldDeleteFarm_WhenFarmExists() {

        when(farmRepository.findById(1L)).thenReturn(Optional.of(farm));

        farmService.delete(1L);

        verify(farmRepository, times(1)).delete(farm);
    }

    /**
     * Test pour vérifier que la méthode delete() lance une exception si la ferme n'existe pas.
     */
    @Test
    void delete_ShouldThrowEntityNotFoundException_WhenFarmDoesNotExist() {

        when(farmRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert: vérifier que l'exception est lancée
        assertThrows(EntityNotFoundException.class, () -> farmService.delete(1L));
        verify(farmRepository, times(1)).findById(1L);
    }

    /**
     * Test pour vérifier la recherche des fermes par critères.
     */
    @Test
    void searchFarms_ShouldReturnFarmResponseDTOList() {

        String query = "Farm A";
        when(searchRepository.findFarmMultiCriteriaSearch(query)).thenReturn(Arrays.asList(farm));
        when(farmMapper.toResponseDTO(farm)).thenReturn(farmResponseDTO);


        List<FarmResponseDTO> result = farmService.searchFarms(query);


        assertNotNull(result, "La liste ne doit pas être nulle.");
        assertEquals(1, result.size(), "La taille de la liste devrait être 1.");
        assertEquals(farmResponseDTO, result.get(0), "Les objets doivent être égaux.");
        verify(searchRepository, times(1)).findFarmMultiCriteriaSearch(query);
    }
}
