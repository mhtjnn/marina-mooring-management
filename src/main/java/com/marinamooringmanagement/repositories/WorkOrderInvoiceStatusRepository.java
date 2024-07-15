package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.metadata.WorkOrderInvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkOrderInvoiceStatusRepository extends JpaRepository<WorkOrderInvoiceStatus, Integer> {
    Optional<WorkOrderInvoiceStatus> findByStatus(String pending);
}
