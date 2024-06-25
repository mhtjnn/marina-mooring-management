package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Mooring;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Mooring entities.
 */
@Repository
public interface MooringRepository extends JpaRepository<Mooring, Integer> {

    Optional<Mooring> findByMooringNumber(final String mooringId);

    Page<Mooring> findAll(final Specification<Mooring> spec, final Pageable pageable);

    List<Mooring> findAll(final Specification<Mooring> spec);
}