package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing WorkOrder entities in the database.
 */
@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Integer> {
}

