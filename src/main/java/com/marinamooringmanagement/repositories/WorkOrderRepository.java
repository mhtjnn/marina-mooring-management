package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.WorkOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Integer> {

    Page<WorkOrder> findAll(final Specification<WorkOrder> specs, final Pageable p);

    List<WorkOrder> findAll(final Specification<WorkOrder> specs);

    Optional<WorkOrder> findByWorkOrderNumber(final String workOrderNumber);
}
