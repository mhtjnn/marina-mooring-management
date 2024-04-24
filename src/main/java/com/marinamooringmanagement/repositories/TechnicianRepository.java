package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing Technician entities in the database.
 */
@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Integer> {

}
