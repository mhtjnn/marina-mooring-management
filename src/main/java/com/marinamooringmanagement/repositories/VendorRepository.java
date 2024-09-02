package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Customer;
import com.marinamooringmanagement.model.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Vendor entities.
 */
@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {

    Page<Vendor> findAll(final Specification<Vendor> spec, final Pageable pageable);

    List<Vendor> findAll(final Specification<Vendor> spec);

    Optional<Vendor> findByCompanyEmail(final String companyEmail);

    Optional<Vendor> findByRemitEmailAddress(final String remitEmailAddress);

    Optional<Vendor> findBySalesRepEmail(final String salesRepEmail);
}