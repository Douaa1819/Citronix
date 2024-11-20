package com.citronix.citronix.repository;

import com.citronix.citronix.entity.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FarmRepository extends JpaRepository<Farm, Long> {
    @Query("SELECT f FROM Farm f WHERE (:name IS NULL OR f.name LIKE CONCAT('%', :name, '%')) AND (:location IS NULL OR f.location LIKE CONCAT('%', :location, '%'))")
    List<Farm> findFarmsByCriteria(
            @Param("name") String name,
            @Param("location") String location
    );
}