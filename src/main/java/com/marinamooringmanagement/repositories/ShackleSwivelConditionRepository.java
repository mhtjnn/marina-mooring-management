package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.ShackleSwivelCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShackleSwivelConditionRepository extends JpaRepository<ShackleSwivelCondition, Integer> {
}
