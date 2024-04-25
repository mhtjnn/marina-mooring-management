package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Mooring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Mooring entities.
 */
@Repository
public interface MooringRepository extends JpaRepository<Mooring, Integer> {
}
