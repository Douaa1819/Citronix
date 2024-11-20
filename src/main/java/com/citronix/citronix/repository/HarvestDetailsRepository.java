package com.citronix.citronix.repository;


import com.citronix.citronix.entity.HarvestDetails;
import com.citronix.citronix.entity.Tree;
import com.citronix.citronix.entity.embedded.HarvestDetailsId;
import com.citronix.citronix.entity.enums.Season;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HarvestDetailsRepository extends JpaRepository<HarvestDetails, HarvestDetailsId> {
    boolean existsByTreeAndHarvestSeason(Tree tree, Season season);

}