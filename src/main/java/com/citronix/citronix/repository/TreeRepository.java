package com.citronix.citronix.repository;

import com.citronix.citronix.entity.Field;
import com.citronix.citronix.entity.Tree;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreeRepository extends JpaRepository<Tree, Long> {
    Integer countByField ( Field field );
}
