package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    Page<Inventory> findAll(final Specification<Inventory> spec, final Pageable p);

}
