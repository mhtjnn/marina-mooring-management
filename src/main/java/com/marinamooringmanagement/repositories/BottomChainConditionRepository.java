package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.metadata.BottomChainCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BottomChainConditionRepository extends JpaRepository<BottomChainCondition, Integer> {
}
