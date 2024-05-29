package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.SizeOfWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeOfWeightRepository extends JpaRepository<SizeOfWeight, Integer> {
}
