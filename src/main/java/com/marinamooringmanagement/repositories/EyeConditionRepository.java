package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.metadata.EyeCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EyeConditionRepository extends JpaRepository<EyeCondition, Integer> {
}
