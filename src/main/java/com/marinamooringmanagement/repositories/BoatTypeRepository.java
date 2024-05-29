package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.BoatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoatTypeRepository extends JpaRepository<BoatType, Integer> {
}
