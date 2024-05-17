package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Vendor entities.
 */
@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {
}