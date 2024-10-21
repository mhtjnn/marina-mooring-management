package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Boatyard;
import com.marinamooringmanagement.model.entity.Mooring;
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

    @Query("SELECT new com.marinamooringmanagement.model.entity.Mooring(" +
            "m.id, m.mooringNumber, m.harborOrArea, m.gpsCoordinates, m.installBottomChainDate, " +
            "m.installTopChainDate, m.installConditionOfEyeDate, m.inspectionDate, m.boatId, m.boatName, " +
            "m.boatSize, bt.id, bt.boatType, m.boatWeight, m.sizeOfWeight, tw.id, tw.type, ec.id, " +
            "ec.condition, tc.id, tc.condition, bc.id, bc.condition, sc.id, sc.condition, " +
            "m.pendantCondition, m.depthAtMeanHighWater, ms.id, ms.status , c.id, c.firstName, " +
            "c.lastName, c.customerId, u.id, u.firstName, u.lastName, byd.id, byd.boatyardName, " +
            "s.id, s.serviceAreaName) " +
            "FROM Mooring m " +
            "LEFT JOIN m.boatType bt " +
            "LEFT JOIN m.typeOfWeight tw " +
            "LEFT JOIN m.eyeCondition ec " +
            "LEFT JOIN m.topChainCondition tc " +
            "LEFT JOIN m.bottomChainCondition bc " +
            "LEFT JOIN m.shackleSwivelCondition sc " +
            "LEFT JOIN m.mooringStatus ms " +
            "LEFT JOIN m.customer c " +
            "LEFT JOIN m.boatyard byd " +
            "LEFT JOIN m.serviceArea s " +
            "JOIN m.user u " +
            "JOIN u.role r " +
            "where (:userId IS NOT NULL AND u.id = :userId AND :boatyardId IS NOT NULL AND :boatyardId = byd.id) " +
            "ORDER BY m.id")
    List<Mooring> findAllMooringForGivenBoatyard(@Param("userId") Integer userId,
                                                 @Param("boatyardId") Integer boatyardId);

    @Query("SELECT COUNT(m) " +
            "FROM Mooring m " +
            "LEFT JOIN m.boatyard byd " +
            "LEFT JOIN m.user u " +
            "where (:userId IS NOT NULL AND u.id = :userId AND :boatyardId IS NOT NULL AND :boatyardId = byd.id)")
    Integer countAllMooringForGivenBoatyard(@Param("userId") Integer userId,
                                                 @Param("boatyardId") Integer boatyardId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Boatyard(" +
            "b.id, b.boatyardName, b.boatyardId, b.address, b.gpsCoordinates, b.mainContact, b.zipCode, b.storageAreas, " +
            "s.id, s.name, " +
            "c.id, c.name, " +
            "u.id, u.firstName, u.lastName) " +
            "FROM Boatyard b " +
            "LEFT JOIN b.user u " +
            "LEFT JOIN b.state s " +
            "LEFT JOIN b.country c " +
            "WHERE (:userId IS NOT NULL AND u.id = :userId) " +
            "AND (:searchText IS NOT NULL AND (" +
            "LOWER(CAST(b.id AS string)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(b.boatyardName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(b.address) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(b.boatyardName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(b.boatyardId) LIKE LOWER(CONCAT('%', :searchText, '%')))) " +
            "ORDER BY b.id")
    List<Boatyard> findAll(@Param("searchText") String searchText,
                           @Param("userId") Integer userId);
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
