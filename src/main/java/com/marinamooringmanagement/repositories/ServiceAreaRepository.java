package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Boatyard;
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

    List<ServiceArea> findAll(Specification<ServiceArea> specification);

    Page<ServiceArea> findAll(Specification<ServiceArea> specification, Pageable pageable);

    Optional<ServiceArea> findByServiceAreaName(String serviceAreaName);

    @Query("SELECT new com.marinamooringmanagement.model.entity.ServiceArea( " +
            "sa.id, sa.serviceAreaName) " +
            "FROM ServiceArea sa " +
            "LEFT JOIN sa.user u " +
            "WHERE (:userId IS NOT NULL AND u.id = :userId) " +
            "ORDER BY sa.id")
    List<ServiceArea> findAllServiceAreaMetadata(@Param("userId") Integer userId);
}
