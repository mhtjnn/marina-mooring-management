package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.metadata.ShackleSwivelCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShackleSwivelConditionRepository extends JpaRepository<ShackleSwivelCondition, Integer> {
}
