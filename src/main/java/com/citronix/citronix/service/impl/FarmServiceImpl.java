package com.citronix.citronix.service.impl;

import com.citronix.citronix.dto.request.FarmRequestDTO;
import com.citronix.citronix.dto.response.FarmResponseDTO;
import com.citronix.citronix.entity.Farm;
import com.citronix.citronix.entity.Field;
import com.citronix.citronix.exception.EntityNotFoundException;
import com.citronix.citronix.mapper.FarmMapper;
import com.citronix.citronix.repository.FarmRepository;
import com.citronix.citronix.repository.FarmSearchRepository;
import com.citronix.citronix.service.FarmService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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
    public List<FarmResponseDTO> findAll() {
        return farmRepository.findAll().stream()
                .map(farmMapper::toResponseDTO)
                .collect(Collectors.toList());
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
        return farmOptional.map(farmMapper::toResponseDTO).orElse(null);
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

    @Transactional
    @Override
    public FarmResponseDTO create(FarmRequestDTO farmRequestDTO) {
        Farm farm = farmMapper.toEntity(farmRequestDTO);
        Farm savedFarm = farmRepository.save(farm);
        if (farm.getFields() != null) {
            for (Field field : farm.getFields()) {
                field.setFarm(farm);
            }
        }

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
        Optional<Farm> farmOptional = farmRepository.findById(id);
        if (farmOptional.isPresent()) {
            Farm farm = farmOptional.get();
            farmMapper.updateFarmFromDto(farmRequestDTO, farm);
            Farm UpdatedFarm = farmRepository.save(farm);
            return farmMapper.toResponseDTO(UpdatedFarm);
        }else {
            throw new EntityNotFoundException("Farm not found with ID: " + id);
        }
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
                .orElseThrow(() -> new EntityNotFoundException("Farm not found with ID: " + id));
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