package com.citronix.citronix.repository;

import com.citronix.citronix.entity.Farm;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmSearchRepository {
    List<Farm> findFarmMultiCriteriaSearch(String query);
}
