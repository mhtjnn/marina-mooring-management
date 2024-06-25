package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Vendor entities.
 */
@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {

    Page<Vendor> findAll(final Specification<Vendor> spec, final Pageable pageable);

    List<Vendor> findAll(final Specification<Vendor> spec);

}