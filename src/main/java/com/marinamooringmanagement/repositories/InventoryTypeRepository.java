package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.InventoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryTypeRepository extends JpaRepository<InventoryType, Integer> {
}
