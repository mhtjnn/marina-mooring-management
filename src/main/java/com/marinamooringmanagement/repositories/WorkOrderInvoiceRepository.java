package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.WorkOrderInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderInvoiceRepository extends JpaRepository<WorkOrderInvoice, Integer> {

    @Query("SELECT new com.marinamooringmanagement.model.entity.WorkOrderInvoice(" +
            "woi.id, woi.invoiceAmount, " +
            "wois.id, wois.status, " +
            "wo.id, wo.workOrderNumber, wo.dueDate, wo.scheduledDate, wo.completedDate, wo.time, wo.problem, " +
            "m.id, m.mooringNumber, " +
            "tu.id, tu.firstName, tu.lastName, " +
            "cou.id, cou.firstName, cou.lastName, " +
            "wos.id, wos.status, wops.id, wops.status, " +
            "c.id, c.firstName, c.lastName, c.customerId, b.id, b.boatyardName, " +
            "u.id, u.firstName, u.lastName) " +
            "FROM WorkOrderInvoice woi " +
            "JOIN woi.workOrder wo " +
            "JOIN wo.mooring m " +
            "JOIN m.customer c " +
            "JOIN m.boatyard b " +
            "JOIN woi.customerOwnerUser u " +
            "LEFT JOIN wo.customerOwnerUser cou " +
            "LEFT JOIN wo.technicianUser tu " +
            "JOIN wo.workOrderStatus wos " +
            "JOIN wo.workOrderPayStatus wops " +
            "JOIN woi.workOrderInvoiceStatus wois " +
            "WHERE (:searchText IS NOT NULL AND " +
            "(wo.workOrderNumber IS NOT NULL AND LOWER(wo.workOrderNumber) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(c.firstName IS NOT NULL AND LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchText, '%'))) OR " +
            "(c.lastName IS NOT NULL AND LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')))) " +
            " AND woi.customerOwnerUser IS NOT NULL AND woi.customerOwnerUser.id = :userId ")
    List<WorkOrderInvoice> findAll(@Param("searchText") String searchText,
                                   @Param("userId") Integer userId);
    List<WorkOrderInvoice> findAll(final Specification<WorkOrderInvoice> spec);
}
