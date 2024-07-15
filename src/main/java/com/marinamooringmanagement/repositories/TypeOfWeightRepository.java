package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.metadata.TypeOfWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeOfWeightRepository extends JpaRepository<TypeOfWeight, Integer> {
}
