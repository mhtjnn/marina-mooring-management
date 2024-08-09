package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Boatyard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
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

    @Query("SELECT new com.marinamooringmanagement.model.entity.Boatyard( " +
            "b.id, b.boatyardId, b.boatyardName) " +
            "FROM Boatyard b " +
            "LEFT JOIN b.user u " +
            "WHERE (:userId IS NOT NULL AND u.id = :userId) " +
            "ORDER BY b.id")
    List<Boatyard> findAllBoatyardMetadata(@Param("userId") Integer userId);

    Page<Boatyard> findAll(final Specification<Boatyard> spec, final Pageable pageable);
    List<Boatyard> findAll(final Specification<Boatyard> spec);

    Optional<Boatyard> findByBoatyardId(final String boatyardId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Boatyard(" +
            "b.id, b.boatyardName, b.boatyardId, u.id, u.firstName, u.lastName) " +
            "FROM Boatyard b " +
            "LEFT JOIN b.user u " +
            "WHERE (:userId IS NOT NULL AND u.id = :userId) " +
            " AND (:boatyardId IS NOT NULL AND b.id = :boatyardId)")
    Optional<Boatyard> findBoatyardWithUserMetadata(@Param("userId") Integer userId,
                                                    @Param("boatyardId") Integer boatyardId);
}
