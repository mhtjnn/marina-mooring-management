package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Boatyard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing BoatYard entities.
 * This interface provides CRUD operations for BoatYard entities using Spring Data JPA.
 */
@Repository
public interface BoatyardRepository extends JpaRepository<Boatyard, Integer> {
    /**
     * Finds a Boatyard entity by its boatyard name.
     *
     * @param boatyardName The name of the boatyard to search for.
     * @return An Optional containing the Boatyard entity if found, or an empty Optional if not found.
     */
    Optional<Boatyard> findByBoatyardName(final String boatyardName);

    Page<Boatyard> findAll(final Specification<Boatyard> spec, final Pageable pageable);
    List<Boatyard> findAll(final Specification<Boatyard> spec);

    Optional<Boatyard> findByBoatyardId(final String boatyardId);
}
