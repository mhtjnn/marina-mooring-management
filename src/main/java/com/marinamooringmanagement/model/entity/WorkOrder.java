package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marinamooringmanagement.model.entity.metadata.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "work_order")
public class WorkOrder extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "work_order_number")
    private String workOrderNumber;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "schedule_date")
    private Date scheduledDate;

    @Column(name = "completed_date")
    private Date completedDate;

    @Column(name = "time")
    private Time time;

    @Column(name = "problem")
    private String problem;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "mooring_id")
    private Mooring mooring;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_user_id")
    private User technicianUser;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_owner_user_id")
    private User customerOwnerUser;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_status_id")
    private WorkOrderStatus workOrderStatus;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_pay_status_id")
    private WorkOrderPayStatus workOrderPayStatus;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workOrder", fetch = FetchType.LAZY)
    private List<Image> imageList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workOrder", fetch = FetchType.LAZY)
    private List<Form> formList;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "workOrder",fetch = FetchType.LAZY)
    @JsonManagedReference
    private WorkOrderInvoice workOrderInvoice;

    public WorkOrder(Integer id, String workOrderNumber, Date dueDate, Date scheduledDate,
                     Date completedDate, Time time, String problem,
                     Integer mooringId, String mooringNumber, String harborOrArea, String gpsCoordinates,
                     Date installBottomChainDate, Date installTopChainDate, Date installConditionOfEyeDate,
                     Date inspectionDate, String boatName, String boatSize,
                     Integer boatTypeId, String boatTypeName,
                     String boatWeight,
                     Integer sizeOfWeight,
                     Integer typeOfWeightId, String typeOfWeightName,
                     Integer eyeConditionId, String eyeConditionName,
                     Integer topChainConditionId, String topChainConditionName,
                     Integer bottomChainConditionId, String bottomChainConditionName,
                     Integer shackleSwivelConditionId, String shackleSwivelConditionName,
                     String pendantCondition, Integer depthAtMeanHighWater,
                     Integer mooringStatusId, String mooringStatusName,
                     Integer customerId, String customerFirstName, String customerLastName, String customerNumber,
                     Integer userId, String userFirstName, String userLastName,
                     Integer boatyardId, String boatyardName,
                     Integer serviceAreaId, String serviceAreaName,
                     Integer technicianUserId, String technicianUserFirstName, String technicianUserLastName,
                     Integer customerOwnerUserId, String customerOwnerUserFirstName, String customerOwnerUserLastName,
                     Integer workOrderStatusId, String workOrderStatusName, Integer workOrderPayStatusId,
                     String workOrderPayStatusName, Integer workOrderInvoiceId
    )
    {
        this.id = id;
        this.workOrderNumber = workOrderNumber;
        this.dueDate = dueDate;
        this.scheduledDate = scheduledDate;
        this.completedDate = completedDate;
        this.time = time;
        this.problem = problem;
        this.mooring =
                Mooring.builder()
                        .id(mooringId)
                        .mooringNumber(mooringNumber)
                        .harborOrArea(harborOrArea)
                        .gpsCoordinates(gpsCoordinates)
                        .installBottomChainDate(installBottomChainDate)
                        .installTopChainDate(installTopChainDate)
                        .installConditionOfEyeDate(installConditionOfEyeDate)
                        .inspectionDate(inspectionDate)
                        .boatName(boatName)
                        .boatSize(boatSize)
                        .boatType(BoatType.builder().id(boatTypeId).boatType(boatTypeName).build())
                        .boatWeight(boatWeight)
                        .sizeOfWeight(sizeOfWeight)
                        .typeOfWeight(TypeOfWeight.builder().id(typeOfWeightId).type(typeOfWeightName).build())
                        .eyeCondition(EyeCondition.builder().id(eyeConditionId).condition(eyeConditionName).build())
                        .topChainCondition(TopChainCondition.builder().id(topChainConditionId).condition(topChainConditionName).build())
                        .bottomChainCondition(BottomChainCondition.builder().id(bottomChainConditionId).condition(bottomChainConditionName).build())
                        .shackleSwivelCondition(ShackleSwivelCondition.builder().id(shackleSwivelConditionId).condition(shackleSwivelConditionName).build())
                        .pendantCondition(pendantCondition)
                        .depthAtMeanHighWater(depthAtMeanHighWater)
                        .mooringStatus(MooringStatus.builder().id(mooringStatusId).status(mooringStatusName).build())
                        .customer(Customer.builder().id(customerId).firstName(customerFirstName).lastName(customerLastName).customerId(customerNumber).build())
                        .user(User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build())
                        .boatyard(Boatyard.builder().id(boatyardId).boatyardName(boatyardName).build())
                        .serviceArea(ServiceArea.builder().id(serviceAreaId).serviceAreaName(serviceAreaName).build())
                        .build();
        this.technicianUser = User.builder().id(technicianUserId).firstName(technicianUserFirstName).lastName(technicianUserLastName).build();
        this.customerOwnerUser = User.builder().id(customerOwnerUserId).firstName(customerOwnerUserFirstName).lastName(customerOwnerUserLastName).build();
        this.workOrderStatus = WorkOrderStatus.builder().id(workOrderStatusId).status(workOrderStatusName).build();
        this.workOrderPayStatus = WorkOrderPayStatus.builder().id(workOrderPayStatusId).status(workOrderPayStatusName).build();
        this.workOrderInvoice = WorkOrderInvoice.builder().id(workOrderInvoiceId).build();
    }
}
