package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.metadata.BoatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoatTypeRepository extends JpaRepository<BoatType, Integer> {
}
