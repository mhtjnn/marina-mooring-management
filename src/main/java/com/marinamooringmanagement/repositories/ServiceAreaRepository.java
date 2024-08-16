package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Boatyard;
import com.marinamooringmanagement.model.entity.Mooring;
import com.marinamooringmanagement.model.entity.ServiceArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceAreaRepository extends JpaRepository<ServiceArea, Integer> {

    @Query("SELECT new com.marinamooringmanagement.model.entity.ServiceArea( " +
            "sa.id, sa.serviceAreaName, sa.address, " +
            "st.id, st.name, " +
            "co.id, co.name, " +
            "sa.zipCode, sa.gpsCoordinates, " +
            "sat.id, sat.type, " +
            "u.id, u.firstName, u.lastName, " +
            "sa.notes) " +
            "FROM ServiceArea sa " +
            "LEFT JOIN sa.state st " +
            "LEFT JOIN sa.country co " +
            "LEFT JOIN sa.serviceAreaType sat " +
            "LEFT JOIN sa.user u " +
            "WHERE (:userId IS NOT NULL AND u.id = :userId) " +
            "AND (:searchText IS NOT NULL AND (" +
            "LOWER(CAST(sa.id AS string)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(sa.serviceAreaName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(st.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(co.name) LIKE LOWER(CONCAT('%', :searchText, '%')))) " +
            "ORDER BY sa.id")
    List<ServiceArea> findAll(@Param("userId") Integer userId,
                              @Param("searchText") String searchText);

    @Query("SELECT COUNT(m) " +
            "FROM Mooring m " +
            "LEFT JOIN m.serviceArea sa " +
            "LEFT JOIN m.user u " +
            "where (:userId IS NOT NULL AND u.id = :userId AND :serviceAreaId IS NOT NULL AND :serviceAreaId = sa.id)")
    Integer countAllMooringForGivenServiceArea(@Param("userId") Integer userId,
                                            @Param("serviceAreaId") Integer serviceAreaId);

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
            "where (:userId IS NOT NULL AND u.id = :userId AND :serviceAreaId IS NOT NULL AND :serviceAreaId = s.id) " +
            "ORDER BY m.id")
    List<Mooring> findAllMooringForGivenServiceArea(@Param("userId") Integer userId,
                                                 @Param("serviceAreaId") Integer serviceAreaId);

    Page<ServiceArea> findAll(Specification<ServiceArea> specification, Pageable pageable);

    Optional<ServiceArea> findByServiceAreaName(String serviceAreaName);

    @Query("SELECT new com.marinamooringmanagement.model.entity.ServiceArea( " +
            "sa.id, sa.serviceAreaName) " +
            "FROM ServiceArea sa " +
            "LEFT JOIN sa.user u " +
            "WHERE (:userId IS NOT NULL AND u.id = :userId) " +
            "ORDER BY sa.id")
    List<ServiceArea> findAllServiceAreaMetadata(@Param("userId") Integer userId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.ServiceArea(" +
            "sa.id, sa.serviceAreaName, u.id, u.firstName, u.lastName) " +
            "FROM ServiceArea sa " +
            "LEFT JOIN sa.user u " +
            "WHERE (:userId IS NOT NULL AND u.id = :userId) " +
            " AND (:serviceAreaId IS NOT NULL AND sa.id = :serviceAreaId)")
    Optional<ServiceArea> findServiceAreaWithUserMetadata(@Param("userId") Integer userId,
                                                    @Param("serviceAreaId") Integer serviceAreaId);
}
