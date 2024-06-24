package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing Customer entities in the database.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Page<Customer> findAll(final Specification<Customer> spec, final Pageable p);

    Optional<Customer> findByEmailAddress(String emailAddress);

    Optional<Customer> findByCustomerId(String customerId);

    Optional<Customer> findByPhone(String phoneNumber);
}