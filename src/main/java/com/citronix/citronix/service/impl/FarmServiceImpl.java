package com.citronix.citronix.service.impl;

import com.citronix.citronix.common.exception.EntityConstraintViolationException;
import com.citronix.citronix.dto.request.FarmRequestDTO;
import com.citronix.citronix.dto.response.FarmResponseDTO;
import com.citronix.citronix.entity.Farm;
import com.citronix.citronix.entity.Field;
import com.citronix.citronix.common.exception.EntityNotFoundException;
import com.citronix.citronix.mapper.FarmMapper;
import com.citronix.citronix.repository.FarmRepository;
import com.citronix.citronix.repository.FarmSearchRepository;
import com.citronix.citronix.service.FarmService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FarmServiceImpl implements FarmService {

    private final FarmRepository farmRepository;
    private final FarmMapper farmMapper;
    private final FarmSearchRepository searchRepository;

    /**
     * Retrieves a list of all farms.
     *
     * This method fetches all farms from the repository and converts them into
     * `FarmResponseDTO` objects. It is useful for listing all available farms.
     *
     * @return a list of `FarmResponseDTO` objects representing all farms.
     */


    @Override
    public Page<FarmResponseDTO> findAll (int pageNum, int pageSize ) {
        return farmRepository.findAll(PageRequest.of(pageNum, pageSize)).map(farmMapper::toResponseDTO);
    }
    /**
     * Finds a farm by its ID.
     *
     * This method searches for a farm in the repository using the provided ID.
     * If the farm is found, it is converted into a `FarmResponseDTO`. If no farm
     * is found, it returns `null`.
     *
     * @param id the ID of the farm to find.
     * @return the `FarmResponseDTO` corresponding to the farm, or `null` if not found.
     */

    @Override
    public FarmResponseDTO findById(Long id) {
        Optional<Farm> farmOptional = farmRepository.findById(id);
        return farmOptional.map(farmMapper::toResponseDTO).orElseThrow(() -> new EntityNotFoundException("Farm", id));
    }


    /**
     * Creates a new farm.
     *
     * This method creates a new farm based on the provided `FarmRequestDTO`. It
     * first maps the DTO to an entity, then saves it to the repository. If the farm
     * contains fields, they are associated with the newly created farm.
     *
     * @param farmRequestDTO the DTO containing the farm details.
     * @return a `FarmResponseDTO` representing the saved farm.
     */


    @Override
    public FarmResponseDTO create(FarmRequestDTO farmRequestDTO) {

        if (farmRequestDTO.totalArea() < 2000) {
            throw new EntityConstraintViolationException("Farm", "Surface", farmRequestDTO.totalArea(), "Farm surface must be at least 2,000 m².");
        }
        Farm farm = farmMapper.toEntity(farmRequestDTO);
        Farm savedFarm = farmRepository.save(farm);

        return farmMapper.toResponseDTO(savedFarm);
    }

    /**
     * Updates an existing farm.
     *
     * This method updates the details of an existing farm. It retrieves the farm
     * using the provided ID, applies the changes from the `FarmRequestDTO`, and
     * saves the updated farm to the repository. If no farm is found with the given
     * ID, an `EntityNotFoundException` is thrown.
     *
     * @param id the ID of the farm to update.
     * @param farmRequestDTO the DTO containing the updated farm details.
     * @return a `FarmResponseDTO` representing the updated farm.
     * @throws EntityNotFoundException if no farm with the given ID is found.
     */

    @Override
    public FarmResponseDTO update(Long id, FarmRequestDTO farmRequestDTO) {
        if (farmRequestDTO.totalArea() < 2000) {
            throw new EntityConstraintViolationException(
                    "Farm",
                    "Total Area",
                    farmRequestDTO.totalArea(),
                    "Farm surface must be at least 2,000 m²."
            );
        }


        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Farm" , id));

        if (farmRequestDTO.name() != null && !farmRequestDTO.name().isEmpty()) {
            farm.setName(farmRequestDTO.name());
        }
        if (farmRequestDTO.location() != null && !farmRequestDTO.location().isEmpty()) {
            farm.setLocation(farmRequestDTO.location());
        }
        if (farmRequestDTO.totalArea() != null) {
            farm.setTotalArea(farmRequestDTO.totalArea());
        }
        if (farmRequestDTO.creationDate() != null) {
            farm.setCreationDate(farmRequestDTO.creationDate());
        }
        Farm updatedFarm = farmRepository.save(farm);

        return farmMapper.toResponseDTO(updatedFarm);
    }


    /**
     * Deletes a farm by its ID.
     *
     * This method deletes the farm with the specified ID from the repository.
     * If the farm is not found, an `EntityNotFoundException` is thrown.
     *
     * @param id the ID of the farm to delete.
     * @throws EntityNotFoundException if no farm with the given ID is found.
     */

    @Override
    @Transactional
    public void delete(Long id) {
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Farm " , id));
        farmRepository.delete(farm);
    }


    /**
     * Searches farms based on a query.
     *
     * This method performs a multi-criteria search for farms using the provided
     * query. It returns a list of `FarmResponseDTO` objects representing farms
     * that match the search criteria.
     *
     * @param query the search query.
     * @return a list of `FarmResponseDTO` objects matching the search query.
     */

    @Override
    public List<FarmResponseDTO> searchFarms(String query) {
        List<Farm> farms = searchRepository.findFarmMultiCriteriaSearch(query);

        return farms.stream()
                .map(farmMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}