package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.metadata.PennantCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PennantConditionRepository extends JpaRepository<PennantCondition, Integer> {
}
