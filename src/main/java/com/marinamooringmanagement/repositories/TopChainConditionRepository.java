package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.TopChainCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopChainConditionRepository extends JpaRepository<TopChainCondition, Integer> {
}