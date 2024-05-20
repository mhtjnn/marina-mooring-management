package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Technician;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for accessing Technician entities in the database.
 */
@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Integer> {

    Page<Technician> findAll(final Specification<Technician> spec, final Pageable pageable);

}
