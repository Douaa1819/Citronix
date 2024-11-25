package com.citronix.citronix.repository;


import com.citronix.citronix.entity.Farm;
import com.citronix.citronix.entity.HarvestDetails;
import com.citronix.citronix.entity.Tree;
import com.citronix.citronix.entity.enums.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HarvestDetailsRepository extends JpaRepository<HarvestDetails, Long> {
    void deleteByHarvestId(Long harvestId);

    List<HarvestDetails> findAll();
    List<HarvestDetails> findByTree_Field_Farm_IdAndAndHarvest_Season(Long farm, Season season);
    List<HarvestDetails> findByTreeAndHarvestSeason(Tree tree, Season season);
}

