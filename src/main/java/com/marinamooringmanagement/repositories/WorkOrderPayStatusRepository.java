package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.WorkOrderPayStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkOrderPayStatusRepository extends JpaRepository<WorkOrderPayStatus, Integer> {
    Optional<WorkOrderPayStatus> findByStatus(final String status);
}
