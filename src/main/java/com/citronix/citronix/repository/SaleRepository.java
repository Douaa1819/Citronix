package com.citronix.citronix.repository;

import com.citronix.citronix.entity.Sale;
import com.citronix.citronix.entity.Tree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByHarvestId(Long harvestId);
}
