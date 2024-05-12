package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Mooring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Mooring entities.
 */
@Repository
public interface MooringRepository extends JpaRepository<Mooring, Integer> {

    Optional<Mooring> findByMooringId(String mooringId);

    void deleteAllByBoatyardName(String boatyardName);

    List<Mooring> findAllByBoatyardName(String boatyardName);
}