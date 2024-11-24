package com.citronix.citronix.TreeTest;

import com.citronix.citronix.common.exception.EntityConstraintViolationException;
import com.citronix.citronix.dto.request.TreeRequestDTO;
import com.citronix.citronix.dto.response.EmbedeedTreeFieldResponseDTO;
import com.citronix.citronix.dto.response.TreeResponseDTO;
import com.citronix.citronix.entity.Field;
import com.citronix.citronix.entity.Tree;
import com.citronix.citronix.common.exception.EntityNotFoundException;
import com.citronix.citronix.mapper.TreeMapper;
import com.citronix.citronix.repository.FieldRepository;
import com.citronix.citronix.repository.TreeRepository;
import com.citronix.citronix.service.impl.TreeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

class TreeServiceImplTest {

    @Mock
    private TreeRepository treeRepository;

    @Mock
    private TreeMapper treeMapper;

    @Mock
    private FieldRepository fieldRepository;

    @InjectMocks
    private TreeServiceImpl treeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldFindAllTrees() {

        PageRequest pageRequest = PageRequest.of(0, 5);
        Tree tree = Tree.builder()
                .id(1L)
                .plantingDate(LocalDate.now().minusYears(5))
                .build();
        EmbedeedTreeFieldResponseDTO fieldResponse = new EmbedeedTreeFieldResponseDTO(1L, "Field A", 100.0, null);
        TreeResponseDTO responseDTO = new TreeResponseDTO(1L, LocalDate.now().minusYears(5), 5, 12.0, fieldResponse);

        when(treeRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(List.of(tree)));
        when(treeMapper.toResponseDTO(tree)).thenReturn(responseDTO);


        Page<TreeResponseDTO> result = treeService.findAll(0, 5);


        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).id());
        verify(treeRepository, times(1)).findAll(pageRequest);
    }


    @Test
    void shouldFindTreeById() {

        Long treeId = 1L;
        Tree tree = Tree.builder()
                .id(treeId)
                .plantingDate(LocalDate.now().minusYears(3))
                .build();

        // Crée un mock ou une instance valide de EmbedeedTreeFieldResponseDTO
        EmbedeedTreeFieldResponseDTO fieldResponse = new EmbedeedTreeFieldResponseDTO(1L, "Field A", 100.0, null);

        TreeResponseDTO responseDTO = new TreeResponseDTO(treeId, LocalDate.now().minusYears(3), 3, 12.0, fieldResponse);

        // Configure les mocks pour le test
        when(treeRepository.findById(treeId)).thenReturn(Optional.of(tree));
        when(treeMapper.toResponseDTO(tree)).thenReturn(responseDTO);


        TreeResponseDTO result = treeService.findById(treeId);


        assertNotNull(result);
        assertEquals(treeId, result.id());
        verify(treeRepository, times(1)).findById(treeId);
    }


    @Test
    void shouldThrowExceptionWhenTreeNotFoundById() {

        Long treeId = 1L;
        when(treeRepository.findById(treeId)).thenReturn(Optional.empty());


        EntityConstraintViolationException exception = assertThrows(EntityConstraintViolationException.class, () -> treeService.findById(treeId));
        assertEquals("Tree 1 not found", exception.getMessage());
        verify(treeRepository, times(1)).findById(treeId);
    }

    @Test
    void shouldCreateTreeSuccessfully() {

        TreeRequestDTO requestDTO = new TreeRequestDTO(LocalDate.now().minusYears(2), 1L);
        Field field = Field.builder().id(1L).build();
        Tree tree = Tree.builder()
                .id(1L)
                .plantingDate(LocalDate.now().minusYears(2))
                .field(field)
                .build();
        EmbedeedTreeFieldResponseDTO fieldResponse = new EmbedeedTreeFieldResponseDTO(1L, "Field A", 100.0, null);
        TreeResponseDTO responseDTO = new TreeResponseDTO(1L, LocalDate.now().minusYears(2), 2, 12.0, fieldResponse);

        when(fieldRepository.findById(1L)).thenReturn(Optional.of(field));
        when(treeMapper.toEntity(requestDTO)).thenReturn(tree);
        when(treeRepository.save(tree)).thenReturn(tree);
        when(treeMapper.toResponseDTO(tree)).thenReturn(responseDTO);


        TreeResponseDTO result = treeService.create(requestDTO);


        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(treeRepository, times(1)).save(tree);
    }

    @Test
    void shouldThrowExceptionWhenFieldNotFound() {

        TreeRequestDTO requestDTO = new TreeRequestDTO(LocalDate.now().minusYears(2), 1L);
        when(fieldRepository.findById(1L)).thenReturn(Optional.empty());


        EntityConstraintViolationException exception = assertThrows(EntityConstraintViolationException.class, () -> treeService.create(requestDTO));
        assertEquals("The 'planting date' attribute of the 'Tree' entity has an invalid value: 'NOVEMBER'. Trees can only be planted between March and May", exception.getMessage());
    }

    @Test
    void shouldDeleteTreeSuccessfully() {

        Long treeId = 1L;
        Tree tree = Tree.builder().id(treeId).build();

        when(treeRepository.findById(treeId)).thenReturn(Optional.of(tree));
        doNothing().when(treeRepository).delete(tree);


        treeService.delete(treeId);


        verify(treeRepository, times(1)).delete(tree);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentTree() {

        Long treeId = 1L;
        when(treeRepository.findById(treeId)).thenReturn(Optional.empty());


        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> treeService.delete(treeId));
        assertEquals("Tree with id 1 not found", exception.getMessage());
    }
}
