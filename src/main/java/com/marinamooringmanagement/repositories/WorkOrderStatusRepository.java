package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.WorkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkOrderStatusRepository extends JpaRepository<WorkOrderStatus, Integer> {
    Optional<WorkOrderStatus> findByStatus(String pendingApproval);
}
