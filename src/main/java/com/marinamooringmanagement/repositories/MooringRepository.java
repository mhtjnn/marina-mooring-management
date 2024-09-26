package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Boatyard;
import com.marinamooringmanagement.model.entity.Customer;
import com.marinamooringmanagement.model.entity.Mooring;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Mooring entities.
 */
@Repository
public interface MooringRepository extends JpaRepository<Mooring, Integer> {

    Optional<Mooring> findByMooringNumber(final String mooringId);

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
            "where (:searchText IS NOT NULL AND" +
            "(LOWER(CAST(m.id AS string)) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(m.mooringNumber IS NOT NULL AND LOWER(m.mooringNumber) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(c.firstName IS NOT NULL AND LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(c.lastName IS NOT NULL AND LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(c.firstName IS NOT NULL AND c.lastName IS NOT NULL AND LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(s.serviceAreaName IS NOT NULL AND LOWER(s.serviceAreaName) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(m.gpsCoordinates IS NOT NULL AND LOWER(m.gpsCoordinates) LIKE LOWER(CONCAT('%', :searchText, '%')))) " +
            "AND (:userId IS NOT NULL AND u.id = :userId) " +
            "ORDER BY m.id")
    List<Mooring> findAll(@Param("searchText") String searchText, @Param("userId") Integer userId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Mooring(" +
            "m.id, m.mooringNumber, m.harborOrArea, m.gpsCoordinates, m.installBottomChainDate, " +
            "m.installTopChainDate, m.installConditionOfEyeDate, m.inspectionDate, m.boatName, " +
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
            "WHERE m.id = :mooringId AND u.id = :userId")
    Optional<Mooring> findById(@Param("mooringId") Integer mooringId,
                               @Param("userId") Integer userId);

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
            "where (:userId IS NOT NULL AND u.id = :userId " +
            "AND c.id = :customerId) " +
            "ORDER BY m.id")
    List<Mooring> fetchMooringsWithCustomerWithoutMooringImages(@Param("customerId") Integer customerId,
                                            @Param("userId") Integer userId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Mooring(" +
            "m.id, m.mooringNumber) " +
            "FROM Mooring m " +
            "LEFT JOIN m.customer c " +
            "LEFT JOIN m.user u " +
            "WHERE ((:customerId IS NOT NULL AND c.id = :customerId) " +
            "AND (:userId is NOT NULL AND u.id = :userId)) " +
            "ORDER BY m.id")
    List<Mooring> findAllMooringsBasedOnCustomerId(@Param("customerId") Integer customerId,
                                                   @Param("userId") Integer userId);

    List<Mooring> findAll(final Specification<Mooring> spec);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Mooring(" +
            "m.id, m.mooringNumber) " +
            "FROM Mooring m " +
            "LEFT JOIN m.boatyard b " +
            "LEFT JOIN m.user u " +
            "WHERE ((:boatyardId IS NOT NULL AND b.id = :boatyardId) " +
            "AND (:userId is NOT NULL AND u.id = :userId)) " +
            "ORDER BY m.id")
    List<Mooring> findAllMooringsBasedOnBoatyardIdMetadata(@Param("boatyardId") Integer boatyardId,
                                                   @Param("userId") Integer userId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Mooring(" +
            "m.id, m.mooringNumber) " +
            "FROM Mooring m " +
            "LEFT JOIN m.user u " +
            "WHERE ((:mooringId IS NOT NULL AND m.id = :mooringId) " +
            "AND (:userId is NOT NULL AND u.id = :userId)) " +
            "ORDER BY m.id")
    Optional<Mooring> findMooringByIdMetadata(@Param("mooringId") Integer mooringId,
                                               @Param("userId") Integer userId);


    @Query("SELECT new com.marinamooringmanagement.model.entity.Customer(" +
            "c.id, c.firstName, c.lastName, c.customerId, u.id, u.firstName, u.lastName) " +
            "FROM Mooring m " +
            "LEFT JOIN m.customer c " +
            "LEFT JOIN m.user u " +
            "WHERE ((:mooringId IS NOT NULL AND m.id = :mooringId) " +
            "AND (:userId is NOT NULL AND u.id = :userId)) " +
            "ORDER BY m.id")
    Optional<Customer> findCustomerByMooringIdMetadata(@Param("mooringId") Integer mooringId,
                                                       @Param("userId") Integer userId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Boatyard(" +
            "b.id, b.boatyardName, b.boatyardId, u.id, u.firstName, u.lastName) " +
            "FROM Mooring m " +
            "LEFT JOIN m.boatyard b " +
            "LEFT JOIN m.user u " +
            "WHERE ((:mooringId IS NOT NULL AND m.id = :mooringId) " +
            "AND (:userId is NOT NULL AND u.id = :userId)) " +
            "ORDER BY m.id")
    Optional<Boatyard> findBoatyardByMooringIdMetadata(@Param("mooringId") Integer mooringId,
                                                       @Param("userId") Integer userId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Mooring(" +
            "m.id, m.mooringNumber) " +
            "FROM Mooring m " +
            "LEFT JOIN m.boatyard b " +
            "LEFT JOIN m.customer c " +
            "LEFT JOIN m.user u " +
            "WHERE ((:boatyardId IS NOT NULL AND b.id = :boatyardId) " +
            "AND (:customerId IS NOT NULL AND c.id = :customerId) " +
            "AND (:userId is NOT NULL AND u.id = :userId)) " +
            "ORDER BY m.id")
    List<Mooring> findAllMooringsBasedOnBoatyardIdAndCustomerIdMetadata(@Param("boatyardId") Integer boatyardId,
                                                           @Param("customerId") Integer customerId,
                                                           @Param("userId") Integer userId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.Mooring(" +
            "m.id, m.mooringNumber) " +
            "FROM Mooring m " +
            "LEFT JOIN m.user u " +
            "WHERE (:userId is NOT NULL AND u.id = :userId) " +
            "ORDER BY m.id")
    List<Mooring> findAllMooringMetadata(@Param("userId") Integer userId);

    Optional<Mooring> findByBoatId(String boatIdStr);
}