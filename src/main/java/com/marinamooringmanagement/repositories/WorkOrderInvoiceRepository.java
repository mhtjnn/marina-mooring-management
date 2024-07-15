package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.WorkOrderInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderInvoiceRepository extends JpaRepository<WorkOrderInvoice, Integer> {
    Page<WorkOrderInvoice> findAll(final Specification<WorkOrderInvoice> spec, final Pageable pageable);
    List<WorkOrderInvoice> findAll(final Specification<WorkOrderInvoice> spec);
}
