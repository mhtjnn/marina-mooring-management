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
            "wo.time, wo.problem, " +
            "m.id, m.mooringNumber, m.harborOrArea, m.gpsCoordinates, m.installBottomChainDate, " +
            "m.installTopChainDate, m.installConditionOfEyeDate, m.inspectionDate, m.boatName, " +
            "m.boatSize, bt.id, bt.boatType, m.boatWeight, m.sizeOfWeight, tw.id, tw.type, ec.id, " +
            "ec.condition, tc.id, tc.condition, bc.id, bc.condition, sc.id, sc.condition, " +
            "m.pendantCondition, m.depthAtMeanHighWater, ms.id, ms.status , c.id, c.firstName, " +
            "c.lastName, c.customerId, u.id, u.firstName, u.lastName, byd.id, byd.boatyardId, byd.boatyardName, " +
            "s.id, s.serviceAreaName, " +
            "tu.id, tu.firstName, tu.lastName, " +
            "cu.id, cu.firstName, cu.lastName, " +
            "wos.id, wos.status, wops.id, wops.status, " +
            "woi.id) " +
            "FROM WorkOrder wo " +
            "LEFT JOIN wo.mooring m " +
            "LEFT JOIN wo.technicianUser tu " +
            "LEFT JOIN wo.customerOwnerUser cu " +
            "LEFT JOIN wo.workOrderStatus wos " +
            "LEFT JOIN wo.workOrderPayStatus wops " +
            "LEFT JOIN wo.workOrderInvoice woi " +
            "LEFT JOIN m.boatType bt " +
            "LEFT JOIN m.typeOfWeight tw " +
            "LEFT JOIN m.eyeCondition ec " +
            "LEFT JOIN m.topChainCondition tc " +
            "LEFT JOIN m.bottomChainCondition bc " +
            "LEFT JOIN m.shackleSwivelCondition sc " +
            "LEFT JOIN m.mooringStatus ms " +
            "LEFT JOIN m.customer c " +
            "LEFT JOIN m.boatyard byd " +
            "LEFT JOIN m.serviceArea s " +
            "LEFT JOIN m.user u " +
            "LEFT JOIN u.role r " +
            "WHERE (:userId IS NOT NULL AND wo.customerOwnerUser.id = :userId) " +
            "AND (:searchText IS NOT NULL AND (" +
            "LOWER(CAST(wo.id AS string)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(wo.problem) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(m.mooringNumber) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(byd.boatyardName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
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
            "wo.time, wo.problem, " +
            "m.id, m.mooringNumber, m.harborOrArea, m.gpsCoordinates, m.installBottomChainDate, " +
            "m.installTopChainDate, m.installConditionOfEyeDate, m.inspectionDate, m.boatName, " +
            "m.boatSize, bt.id, bt.boatType, m.boatWeight, m.sizeOfWeight, tw.id, tw.type, ec.id, " +
            "ec.condition, tc.id, tc.condition, bc.id, bc.condition, sc.id, sc.condition, " +
            "m.pendantCondition, m.depthAtMeanHighWater, ms.id, ms.status , c.id, c.firstName, " +
            "c.lastName, c.customerId, u.id, u.firstName, u.lastName, byd.id, byd.boatyardId, byd.boatyardName, " +
            "s.id, s.serviceAreaName, " +
            "tu.id, tu.firstName, tu.lastName, " +
            "cu.id, cu.firstName, cu.lastName, " +
            "wos.id, wos.status, wops.id, wops.status, " +
            "woi.id) " +
            "FROM WorkOrder wo " +
            "LEFT JOIN wo.mooring m " +
            "LEFT JOIN wo.technicianUser tu " +
            "LEFT JOIN wo.customerOwnerUser cu " +
            "LEFT JOIN wo.workOrderStatus wos " +
            "LEFT JOIN wo.workOrderPayStatus wops " +
            "LEFT JOIN wo.workOrderInvoice woi " +
            "LEFT JOIN m.boatType bt " +
            "LEFT JOIN m.typeOfWeight tw " +
            "LEFT JOIN m.eyeCondition ec " +
            "LEFT JOIN m.topChainCondition tc " +
            "LEFT JOIN m.bottomChainCondition bc " +
            "LEFT JOIN m.shackleSwivelCondition sc " +
            "LEFT JOIN m.mooringStatus ms " +
            "LEFT JOIN m.customer c " +
            "LEFT JOIN m.boatyard byd " +
            "LEFT JOIN m.serviceArea s " +
            "LEFT JOIN m.user u " +
            "LEFT JOIN u.role r " +
            "WHERE (:userId IS NOT NULL AND wo.technicianUser.id = :userId) " +
            "AND (:searchText IS NOT NULL AND (" +
            "LOWER(CAST(wo.id AS string)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(wo.problem) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(m.mooringNumber) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(byd.boatyardName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(s.serviceAreaName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(CONCAT(tu.firstName, ' ', tu.lastName)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(tu.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(tu.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')))) " +
            "AND ((:showCompletedWorkOrders = 'Yes' AND wos.status LIKE 'Completed') OR (:showCompletedWorkOrders = 'No' AND wos.status NOT LIKE 'Completed')) " +
            "ORDER BY wo.id")
    List<WorkOrder> findAllByTechnicianUser(@Param("searchText") String searchText,
                                            @Param("userId") Integer userId,
                                            @Param("showCompletedWorkOrders") String showCompletedWorkOrder);

    @Query("SELECT new com.marinamooringmanagement.model.entity.WorkOrder(" +
            "wo.id, wo.workOrderNumber, wo.dueDate, wo.scheduledDate, wo.completedDate, " +
            "wo.time, wo.problem, " +
            "m.id, m.mooringNumber, m.harborOrArea, m.gpsCoordinates, m.installBottomChainDate, " +
            "m.installTopChainDate, m.installConditionOfEyeDate, m.inspectionDate, m.boatName, " +
            "m.boatSize, bt.id, bt.boatType, m.boatWeight, m.sizeOfWeight, tw.id, tw.type, ec.id, " +
            "ec.condition, tc.id, tc.condition, bc.id, bc.condition, sc.id, sc.condition, " +
            "m.pendantCondition, m.depthAtMeanHighWater, ms.id, ms.status , c.id, c.firstName, " +
            "c.lastName, c.customerId, u.id, u.firstName, u.lastName, byd.id, byd.boatyardId, byd.boatyardName, " +
            "s.id, s.serviceAreaName, " +
            "tu.id, tu.firstName, tu.lastName, " +
            "cu.id, cu.firstName, cu.lastName, " +
            "wos.id, wos.status, wops.id, wops.status, " +
            "woi.id) " +
            "FROM WorkOrder wo " +
            "LEFT JOIN wo.mooring m " +
            "LEFT JOIN wo.technicianUser tu " +
            "LEFT JOIN wo.customerOwnerUser cu " +
            "LEFT JOIN wo.workOrderStatus wos " +
            "LEFT JOIN wo.workOrderPayStatus wops " +
            "LEFT JOIN wo.workOrderInvoice woi " +
            "LEFT JOIN m.boatType bt " +
            "LEFT JOIN m.typeOfWeight tw " +
            "LEFT JOIN m.eyeCondition ec " +
            "LEFT JOIN m.topChainCondition tc " +
            "LEFT JOIN m.bottomChainCondition bc " +
            "LEFT JOIN m.shackleSwivelCondition sc " +
            "LEFT JOIN m.mooringStatus ms " +
            "LEFT JOIN m.customer c " +
            "LEFT JOIN m.boatyard byd " +
            "LEFT JOIN m.serviceArea s " +
            "LEFT JOIN m.user u " +
            "LEFT JOIN u.role r " +
            "WHERE (:userId IS NOT NULL AND wo.customerOwnerUser.id = :userId) " +
            "AND (:searchText IS NOT NULL AND (" +
            "LOWER(CAST(wo.id AS string)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(wo.problem) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(m.mooringNumber) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(byd.boatyardName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(CONCAT(tu.firstName, ' ', tu.lastName)) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(tu.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(tu.lastName) LIKE LOWER(CONCAT('%', :searchText, '%')))) " +
            "AND ((:showCompletedWorkOrders = 'Yes' AND wos.status LIKE 'Completed') OR (:showCompletedWorkOrders = 'No' AND wos.status NOT LIKE 'Completed')) " +
            "AND ((:workOrderPayStatusId IS NOT NULL AND wops.id IS NOT NULL AND wops.id = :workOrderPayStatusId)) " +
            "ORDER BY wo.id")
    List<WorkOrder> findAllWorkOrderWithPayStatus(@Param("searchText") String searchText,
                            @Param("userId") Integer userId,
                            @Param("showCompletedWorkOrders") String showCompletedWorkOrder,
                            @Param("workOrderPayStatusId") Integer workOrderPayStatusId);

    @Query("SELECT new com.marinamooringmanagement.model.entity.WorkOrder(" +
            "wo.id, wo.workOrderNumber, wo.dueDate, wo.scheduledDate, wo.completedDate, " +
            "wo.time, wo.problem, " +
            "m.id, m.mooringNumber, m.harborOrArea, m.gpsCoordinates, m.installBottomChainDate, " +
            "m.installTopChainDate, m.installConditionOfEyeDate, m.inspectionDate, m.boatName, " +
            "m.boatSize, bt.id, bt.boatType, m.boatWeight, m.sizeOfWeight, tw.id, tw.type, ec.id, " +
            "ec.condition, tc.id, tc.condition, bc.id, bc.condition, sc.id, sc.condition, " +
            "m.pendantCondition, m.depthAtMeanHighWater, ms.id, ms.status , c.id, c.firstName, " +
            "c.lastName, c.customerId, u.id, u.firstName, u.lastName, byd.id, byd.boatyardId, byd.boatyardName, " +
            "s.id, s.serviceAreaName, " +
            "tu.id, tu.firstName, tu.lastName, " +
            "cu.id, cu.firstName, cu.lastName, " +
            "wos.id, wos.status, wops.id, wops.status, " +
            "woi.id) " +
            "FROM WorkOrder wo " +
            "LEFT JOIN wo.mooring m " +
            "LEFT JOIN wo.technicianUser tu " +
            "LEFT JOIN wo.customerOwnerUser cu " +
            "LEFT JOIN wo.workOrderStatus wos " +
            "LEFT JOIN wo.workOrderPayStatus wops " +
            "LEFT JOIN wo.workOrderInvoice woi " +
            "LEFT JOIN m.boatType bt " +
            "LEFT JOIN m.typeOfWeight tw " +
            "LEFT JOIN m.eyeCondition ec " +
            "LEFT JOIN m.topChainCondition tc " +
            "LEFT JOIN m.bottomChainCondition bc " +
            "LEFT JOIN m.shackleSwivelCondition sc " +
            "LEFT JOIN m.mooringStatus ms " +
            "LEFT JOIN m.customer c " +
            "LEFT JOIN m.boatyard byd " +
            "LEFT JOIN m.serviceArea s " +
            "LEFT JOIN m.user u " +
            "LEFT JOIN u.role r " +
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
            "wo.time, wo.problem, " +
            "m.id, m.mooringNumber, m.harborOrArea, m.gpsCoordinates, m.installBottomChainDate, " +
            "m.installTopChainDate, m.installConditionOfEyeDate, m.inspectionDate, m.boatName, " +
            "m.boatSize, bt.id, bt.boatType, m.boatWeight, m.sizeOfWeight, tw.id, tw.type, ec.id, " +
            "ec.condition, tc.id, tc.condition, bc.id, bc.condition, sc.id, sc.condition, " +
            "m.pendantCondition, m.depthAtMeanHighWater, ms.id, ms.status , c.id, c.firstName, " +
            "c.lastName, c.customerId, u.id, u.firstName, u.lastName, byd.id, byd.boatyardId, byd.boatyardName, " +
            "s.id, s.serviceAreaName, " +
            "tu.id, tu.firstName, tu.lastName, " +
            "cu.id, cu.firstName, cu.lastName, " +
            "wos.id, wos.status, wops.id, wops.status, " +
            "woi.id) " +
            "FROM WorkOrder wo " +
            "LEFT JOIN wo.mooring m " +
            "LEFT JOIN wo.technicianUser tu " +
            "LEFT JOIN wo.customerOwnerUser cu " +
            "LEFT JOIN wo.workOrderStatus wos " +
            "LEFT JOIN wo.workOrderPayStatus wops " +
            "LEFT JOIN wo.workOrderInvoice woi " +
            "LEFT JOIN m.boatType bt " +
            "LEFT JOIN m.typeOfWeight tw " +
            "LEFT JOIN m.eyeCondition ec " +
            "LEFT JOIN m.topChainCondition tc " +
            "LEFT JOIN m.bottomChainCondition bc " +
            "LEFT JOIN m.shackleSwivelCondition sc " +
            "LEFT JOIN m.mooringStatus ms " +
            "LEFT JOIN m.customer c " +
            "LEFT JOIN m.boatyard byd " +
            "LEFT JOIN m.serviceArea s " +
            "LEFT JOIN m.user u " +
            "LEFT JOIN u.role r " +
            "WHERE ((:customerOwnerUserId IS NOT NULL AND wo.customerOwnerUser.id IS NOT NULL AND wo.customerOwnerUser.id = :customerOwnerUserId) " +
            "AND (:technicianUserId IS NOT NULL AND tu.id IS NOT NULL AND tu.id = :technicianUserId) " +
            "AND ((:showCompletedWorkOrders = 'Yes' AND wos.status LIKE 'Completed') OR (:showCompletedWorkOrders = 'No' AND wos.status NOT LIKE 'Completed'))) " +
            "ORDER BY wo.id")
    List<WorkOrder> findWorkOrderForGivenTechnicianWithoutDateFilter(@Param("technicianUserId") Integer technicianUserId,
                                                                  @Param("customerOwnerUserId") Integer customerOwnerUserId,
                                                                  @Param("showCompletedWorkOrders") String showCompletedWorkOrder);

    List<WorkOrder> findAll(final Specification<WorkOrder> specs);

    @Query("SELECT COUNT(wo) " +
            "FROM WorkOrder wo " +
            "LEFT JOIN wo.workOrderStatus wos " +
            "WHERE wo.technicianUser.id = :technicianUserId " +
            "AND wo.customerOwnerUser.id = :customerOwnerUserId " +
            "AND ((:showCompletedWorkOrders = 'Yes' AND wos.status LIKE 'Completed') OR (:showCompletedWorkOrders = 'No' AND wos.status NOT LIKE 'Completed')) ")
    Integer countWorkOrderForGivenTechnician(@Param("technicianUserId") Integer technicianUserId,
                                             @Param("customerOwnerUserId") Integer customerOwnerUserId,
                                             @Param("showCompletedWorkOrders") String showCompletedWorkOrders);

    Optional<WorkOrder> findByWorkOrderNumber(final String workOrderNumber);

    @Query("SELECT new com.marinamooringmanagement.model.entity.WorkOrder(" +
            "wo.id, wo.workOrderNumber, wo.dueDate, wo.scheduledDate, wo.time, wo.problem, " +
            "m.mooringNumber, " +
            "c.firstName, c.lastName, c.customerId, " +
            "wos.status, " +
            "tu.firstName, tu.lastName, tu.email)" +
            "FROM WorkOrder wo " +
            "LEFT JOIN wo.technicianUser tu " +
            "LEFT JOIN wo.mooring m " +
            "LEFT JOIN wo.workOrderStatus wos " +
            "LEFT JOIN m.customer c " +
            "WHERE (wo.dueDate = :after30DaysDate) "
    )
    List<WorkOrder> findAllWorkOrderWithOpenWorkOrderWithDateNotification(@Param("after30DaysDate") Date after30DaysDate);

    @Query("SELECT new com.marinamooringmanagement.model.entity.WorkOrder(" +
            "wo.id, wo.workOrderNumber, wo.dueDate, wo.scheduledDate, wo.completedDate, " +
            "wo.time, wo.problem, " +
            "m.id, m.mooringNumber, m.harborOrArea, m.gpsCoordinates, m.installBottomChainDate, " +
            "m.installTopChainDate, m.installConditionOfEyeDate, m.inspectionDate, m.boatName, " +
            "m.boatSize, bt.id, bt.boatType, m.boatWeight, m.sizeOfWeight, tw.id, tw.type, ec.id, " +
            "ec.condition, tc.id, tc.condition, bc.id, bc.condition, sc.id, sc.condition, " +
            "m.pendantCondition, m.depthAtMeanHighWater, ms.id, ms.status , c.id, c.firstName, " +
            "c.lastName, c.customerId, u.id, u.firstName, u.lastName, byd.id, byd.boatyardId, byd.boatyardName, " +
            "s.id, s.serviceAreaName, " +
            "tu.id, tu.firstName, tu.lastName, " +
            "cu.id, cu.firstName, cu.lastName, " +
            "wos.id, wos.status, wops.id, wops.status, " +
            "woi.id) " +
            "FROM WorkOrder wo " +
            "LEFT JOIN wo.mooring m " +
            "LEFT JOIN wo.technicianUser tu " +
            "LEFT JOIN wo.customerOwnerUser cu " +
            "LEFT JOIN wo.workOrderStatus wos " +
            "LEFT JOIN wo.workOrderPayStatus wops " +
            "LEFT JOIN wo.workOrderInvoice woi " +
            "LEFT JOIN m.boatType bt " +
            "LEFT JOIN m.typeOfWeight tw " +
            "LEFT JOIN m.eyeCondition ec " +
            "LEFT JOIN m.topChainCondition tc " +
            "LEFT JOIN m.bottomChainCondition bc " +
            "LEFT JOIN m.shackleSwivelCondition sc " +
            "LEFT JOIN m.mooringStatus ms " +
            "LEFT JOIN m.customer c " +
            "LEFT JOIN m.boatyard byd " +
            "LEFT JOIN m.serviceArea s " +
            "LEFT JOIN m.user u " +
            "LEFT JOIN u.role r " +
            "WHERE (:userId IS NOT NULL AND wo.customerOwnerUser.id = :userId) " +
            "AND (:workOrderId IS NOT NULL AND wo.id = :workOrderId)"
    )
    Optional<WorkOrder> findByIdWithBigData(
            @Param("workOrderId") Integer workOrderId,
            @Param("userId") Integer userId
    );
}
