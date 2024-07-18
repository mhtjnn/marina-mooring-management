package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.QuickbookCustomer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuickbookCustomerRepository extends JpaRepository<QuickbookCustomer, Integer> {

    Page<QuickbookCustomer> findAll(Specification<QuickbookCustomer> spec, Pageable p);

    List<QuickbookCustomer> findAll(Specification<QuickbookCustomer> spec);

    Optional<QuickbookCustomer> findByQuickbookCustomerId(String quickbookCustomerId);
}
