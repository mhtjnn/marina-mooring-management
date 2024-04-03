package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.BoatYard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing BoatYard entities.
 * This interface provides CRUD operations for BoatYard entities using Spring Data JPA.
 */
@Repository
public interface BoatYardRepository extends JpaRepository<BoatYard,Integer> {
    
}
