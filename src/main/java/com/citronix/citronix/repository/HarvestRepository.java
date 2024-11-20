package com.citronix.citronix.repository;

import com.citronix.citronix.entity.Harvest;
import com.citronix.citronix.entity.enums.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HarvestRepository extends JpaRepository<Harvest, Long> {
    List<Harvest> findBySeason(Season season);

}