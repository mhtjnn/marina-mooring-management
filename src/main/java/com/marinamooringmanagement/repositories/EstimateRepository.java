package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Estimate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstimateRepository extends JpaRepository<Estimate, Integer> {

    Page<Estimate> findAll(final Specification<Estimate> specs, final Pageable p);

}
