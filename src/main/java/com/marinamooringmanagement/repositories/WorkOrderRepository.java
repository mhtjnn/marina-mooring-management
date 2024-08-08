package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.WorkOrder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Integer> {

    @Query("SELECT new com.marinamooringmanagement.model.entity.WorkOrder(" +
            "wo.id, wo.workOrderNumber, wo.dueDate, wo.scheduledDate, wo.completedDate, " +
            "wo.time, wo.problem, m.id, m.mooringNumber, " +
            "tu.id, tu.firstName, tu.lastName, " +
            "cu.id, cu.firstName, cu.lastName, " +
            "wos.id, wos.status, wops.id, wops.status, " +
            "woi.id, c.id, c.firstName, c.lastName, c.customerId, b.id, b.boatyardName) " +
            "FROM WorkOrder wo " +
            "LEFT JOIN wo.mooring m " +
            "LEFT JOIN m.boatyard b " +
            "LEFT JOIN m.customer c " +
            "LEFT JOIN wo.technicianUser tu " +
            "LEFT JOIN wo.customerOwnerUser cu " +
            "LEFT JOIN wo.workOrderStatus wos " +
            "LEFT JOIN wo.workOrderPayStatus wops " +
            "LEFT JOIN wo.workOrderInvoice woi " +
            "WHERE (:userId IS NOT NULL AND wo.customerOwnerUser.id = :userId) " +
            "AND (:searchText IS NOT NULL AND (" +
            "LOWER(CAST(wo.id AS string)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(wo.problem) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(m.mooringNumber) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(b.boatyardName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(CONCAT(tu.firstName, ' ', tu.lastName)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(tu.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(tu.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')))) " +
            "AND ((:showCompletedWorkOrders = 'Yes' AND wos.status LIKE 'Completed') OR (:showCompletedWorkOrders = 'No' AND wos.status NOT LIKE 'Completed')) " +
            "ORDER BY wo.id")
    List<WorkOrder> findAll(@Param("searchText") String searchText,
                            @Param("userId") Integer userId,
                            @Param("showCompletedWorkOrders") String showCompletedWorkOrder);

    @Query("SELECT new com.marinamooringmanagement.model.entity.WorkOrder(" +
            "wo.id, wo.workOrderNumber, wo.dueDate, wo.scheduledDate, wo.completedDate, " +
            "wo.time, wo.problem, m.id, m.mooringNumber, " +
            "tu.id, tu.firstName, tu.lastName, " +
            "cu.id, cu.firstName, cu.lastName, " +
            "wos.id, wos.status, wops.id, wops.status, " +
            "woi.id, c.id, c.firstName, c.lastName, c.customerId, b.id, b.boatyardName) " +
            "FROM WorkOrder wo " +
            "LEFT JOIN wo.mooring m " +
            "LEFT JOIN m.boatyard b " +
            "LEFT JOIN m.customer c " +
            "LEFT JOIN wo.technicianUser tu " +
            "LEFT JOIN wo.customerOwnerUser cu " +
            "LEFT JOIN wo.workOrderStatus wos " +
            "LEFT JOIN wo.workOrderPayStatus wops " +
            "LEFT JOIN wo.workOrderInvoice woi " +
            "WHERE ((:customerOwnerUserId IS NOT NULL AND wo.customerOwnerUser.id IS NOT NULL AND wo.customerOwnerUser.id = :customerOwnerUserId) " +
            "AND (:technicianUserId IS NOT NULL AND tu.id IS NOT NULL AND tu.id = :technicianUserId) " +
            "AND ((:showCompletedWorkOrders = 'Yes' AND wos.status LIKE 'Completed') OR (:showCompletedWorkOrders = 'No' AND wos.status NOT LIKE 'Completed')) " +
            "AND (wo.scheduledDate BETWEEN :givenScheduledDate AND :givenDueDate) " +
            "AND (wo.dueDate BETWEEN :givenScheduledDate AND :givenDueDate)) " +
            "ORDER BY wo.id")
    List<WorkOrder> findWorkOrderForGivenTechnicianWithDateFilter(@Param("technicianUserId") Integer technicianUserId,
                                                    @Param("customerOwnerUserId") Integer customerOwnerUserId,
                                                    @Param("givenScheduledDate") Date givenScheduledDate,
                                                    @Param("givenDueDate") Date givenDueDate,
                                                    @Param("showCompletedWorkOrders") String showCompletedWorkOrder);

    @Query("SELECT new com.marinamooringmanagement.model.entity.WorkOrder(" +
            "wo.id, wo.workOrderNumber, wo.dueDate, wo.scheduledDate, wo.completedDate, " +
            "wo.time, wo.problem, m.id, m.mooringNumber, " +
            "tu.id, tu.firstName, tu.lastName, " +
            "cu.id, cu.firstName, cu.lastName, " +
            "wos.id, wos.status, wops.id, wops.status, " +
            "woi.id, c.id, c.firstName, c.lastName, c.customerId, b.id, b.boatyardName) " +
            "FROM WorkOrder wo " +
            "LEFT JOIN wo.mooring m " +
            "LEFT JOIN m.boatyard b " +
            "LEFT JOIN m.customer c " +
            "LEFT JOIN wo.technicianUser tu " +
            "LEFT JOIN wo.customerOwnerUser cu " +
            "LEFT JOIN wo.workOrderStatus wos " +
            "LEFT JOIN wo.workOrderPayStatus wops " +
            "LEFT JOIN wo.workOrderInvoice woi " +
            "WHERE ((:customerOwnerUserId IS NOT NULL AND wo.customerOwnerUser.id IS NOT NULL AND wo.customerOwnerUser.id = :customerOwnerUserId) " +
            "AND (:technicianUserId IS NOT NULL AND tu.id IS NOT NULL AND tu.id = :technicianUserId) " +
            "AND ((:showCompletedWorkOrders = 'Yes' AND wos.status LIKE 'Completed') OR (:showCompletedWorkOrders = 'No' AND wos.status NOT LIKE 'Completed'))) " +
            "ORDER BY wo.id")
    List<WorkOrder> findWorkOrderForGivenTechnicianWithoutDateFilter(@Param("technicianUserId") Integer technicianUserId,
                                                                  @Param("customerOwnerUserId") Integer customerOwnerUserId,
                                                                  @Param("showCompletedWorkOrders") String showCompletedWorkOrder);

    @Query("SELECT new com.marinamooringmanagement.model.entity.WorkOrder(" +
            "wo.id, wo.workOrderNumber, wo.dueDate, wo.scheduledDate, wo.completedDate, " +
            "wo.time, wo.problem, m.id, m.mooringNumber, " +
            "tu.id, tu.firstName, tu.lastName, " +
            "cu.id, cu.firstName, cu.lastName, " +
            "wos.id, wos.status, wops.id, wops.status, " +
            "woi.id, c.id, c.firstName, c.lastName, c.customerId, b.id, b.boatyardName) " +
            "FROM WorkOrder wo " +
            "LEFT JOIN wo.mooring m " +
            "LEFT JOIN m.boatyard b " +
            "LEFT JOIN m.customer c " +
            "LEFT JOIN wo.technicianUser tu " +
            "LEFT JOIN wo.customerOwnerUser cu " +
            "LEFT JOIN wo.workOrderStatus wos " +
            "LEFT JOIN wo.workOrderPayStatus wops " +
            "LEFT JOIN wo.workOrderInvoice woi " +
            "WHERE ((:customerOwnerUserId IS NOT NULL AND wo.customerOwnerUser.id IS NOT NULL AND wo.customerOwnerUser.id = :customerOwnerUserId) " +
            "AND ((:showCompletedWorkOrders = 'Yes' AND wos.status LIKE 'Completed') OR (:showCompletedWorkOrders = 'No' AND wos.status NOT LIKE 'Completed')) " +
            "AND (wo.scheduledDate BETWEEN :givenScheduledDate AND :givenDueDate) " +
            "AND (wo.dueDate BETWEEN :givenScheduledDate AND :givenDueDate)) " +
            "ORDER BY wo.id")
    List<WorkOrder> findWorkOrderWithDateFilter(@Param("customerOwnerUserId") Integer customerOwnerUserId,
                                                @Param("givenScheduledDate") Date givenScheduledDate,
                                                @Param("givenDueDate") Date givenDueDate,
                                                @Param("showCompletedWorkOrders") String showCompletedWorkOrder);

    List<WorkOrder> findAll(final Specification<WorkOrder> specs);

    Optional<WorkOrder> findByWorkOrderNumber(final String workOrderNumber);
}
