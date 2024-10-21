package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marinamooringmanagement.model.entity.metadata.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "work_order")
public class WorkOrder extends Base {

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

    @Column(name = "reason_for_denial")
    private String reasonForDenial;

    @Column(name = "cost")
    private BigDecimal cost;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "mooring_id")
    @JsonBackReference
    @ToString.Exclude
    private Mooring mooring;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    @ToString.Exclude
    private Customer customer;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "boatyard_id", nullable = true)
    @JsonBackReference
    @ToString.Exclude
    private Boatyard boatyard;

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
    @JsonManagedReference
    @ToString.Exclude
    private List<Image> imageList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workOrder", fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<Form> formList;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "workOrder", fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private WorkOrderInvoice workOrderInvoice;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workOrder", fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<Inventory> inventoryList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workOrder", fetch = FetchType.LAZY)
    private List<VoiceMEMO> voiceMEMOList;

    public WorkOrder(Integer id, String workOrderNumber, Date dueDate, Date scheduledDate,
                     Date completedDate, Time time, String problem, BigDecimal cost,
                     Integer mooringId, String mooringNumber, String harborOrArea, String gpsCoordinates,
                     Date installBottomChainDate, Date installTopChainDate, Date installConditionOfEyeDate,
                     Date inspectionDate, String boatId, String boatName, String boatSize,
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
                     Integer boatyardId, String boatyardNumber, String boatyardName,
                     Integer serviceAreaId, String serviceAreaName,
                     Integer technicianUserId, String technicianUserFirstName, String technicianUserLastName,
                     Integer customerOwnerUserId, String customerOwnerUserFirstName, String customerOwnerUserLastName,
                     Integer workOrderStatusId, String workOrderStatusName, Integer workOrderPayStatusId,
                     String workOrderPayStatusName, Integer workOrderInvoiceId
    ) {
        this.id = id;
        this.workOrderNumber = workOrderNumber;
        this.dueDate = dueDate;
        this.scheduledDate = scheduledDate;
        this.completedDate = completedDate;
        this.cost = cost;
        this.time = time;
        this.problem = problem;
        if (mooringId != null) this.mooring =
                Mooring.builder()
                        .id(mooringId)
                        .mooringNumber(mooringNumber)
                        .harborOrArea(harborOrArea)
                        .gpsCoordinates(gpsCoordinates)
                        .installBottomChainDate(installBottomChainDate)
                        .installTopChainDate(installTopChainDate)
                        .installConditionOfEyeDate(installConditionOfEyeDate)
                        .inspectionDate(inspectionDate)
                        .boatId(boatId)
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
                        .user(User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build())
                        .serviceArea(ServiceArea.builder().id(serviceAreaId).serviceAreaName(serviceAreaName).build())
                        .build();
        if (customerId != null)
            this.customer = Customer.builder().id(customerId).firstName(customerFirstName).lastName(customerLastName).customerId(customerNumber).build();
        if (boatyardId != null)
            this.boatyard = Boatyard.builder().id(boatyardId).boatyardId(boatyardNumber).boatyardName(boatyardName).build();
        if (technicianUserId != null)
            this.technicianUser = User.builder().id(technicianUserId).firstName(technicianUserFirstName).lastName(technicianUserLastName).build();
        if (customerOwnerUserId != null)
            this.customerOwnerUser = User.builder().id(customerOwnerUserId).firstName(customerOwnerUserFirstName).lastName(customerOwnerUserLastName).build();
        if (workOrderStatusId != null)
            this.workOrderStatus = WorkOrderStatus.builder().id(workOrderStatusId).status(workOrderStatusName).build();
        if (workOrderPayStatusId != null)
            this.workOrderPayStatus = WorkOrderPayStatus.builder().id(workOrderPayStatusId).status(workOrderPayStatusName).build();
        if (workOrderInvoiceId != null)
            this.workOrderInvoice = WorkOrderInvoice.builder().id(workOrderInvoiceId).build();
    }

    public WorkOrder(Integer id, String workOrderNumber, Date dueDate, Date scheduledDate,
                     Date completedDate, Time time, String problem, BigDecimal cost,
                     Integer mooringId, String mooringNumber, String harborOrArea, String gpsCoordinates,
                     Date installBottomChainDate, Date installTopChainDate, Date installConditionOfEyeDate,
                     Date inspectionDate, String boatId, String boatName, String boatSize,
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
                     Integer customerId, String customerFirstName, String customerLastName, String customerNumber, String customerPhoneNumber, String customerAddress,
                     Integer customerStateId, String customerStateName,
                     Integer customerCountryId, String customerCountryName,
                     Integer userId, String userFirstName, String userLastName,
                     Integer boatyardId, String boatyardNumber, String boatyardName, String boatyardAddress,
                     Integer boatyardStateId, String boatyardStateName,
                     Integer boatyardCountryId, String boatyardCountryName,
                     Integer serviceAreaId, String serviceAreaName,
                     Integer technicianUserId, String technicianUserFirstName, String technicianUserLastName,
                     Integer customerOwnerUserId, String customerOwnerUserFirstName, String customerOwnerUserLastName,
                     Integer workOrderStatusId, String workOrderStatusName, Integer workOrderPayStatusId,
                     String workOrderPayStatusName, Integer workOrderInvoiceId
    ) {
        this.id = id;
        this.workOrderNumber = workOrderNumber;
        this.dueDate = dueDate;
        this.scheduledDate = scheduledDate;
        this.completedDate = completedDate;
        this.time = time;
        this.problem = problem;
        this.cost = cost;
        if (mooringId != null) this.mooring =
                Mooring.builder()
                        .id(mooringId)
                        .mooringNumber(mooringNumber)
                        .harborOrArea(harborOrArea)
                        .gpsCoordinates(gpsCoordinates)
                        .installBottomChainDate(installBottomChainDate)
                        .installTopChainDate(installTopChainDate)
                        .installConditionOfEyeDate(installConditionOfEyeDate)
                        .inspectionDate(inspectionDate)
                        .boatId(boatId)
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
                        .user(User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build())
                        .serviceArea(ServiceArea.builder().id(serviceAreaId).serviceAreaName(serviceAreaName).build())
                        .build();
        if (customerId != null) {
            Customer customer1 = Customer.builder().id(customerId).firstName(customerFirstName).lastName(customerLastName).customerId(customerNumber).phone(customerPhoneNumber).address(customerAddress).build();

            if (customerStateId != null) {
                State customerState = State.builder()
                        .id(customerStateId)
                        .name(customerStateName)
                        .build();

                customer1.setState(customerState);
            }

            if (customerCountryId != null) {
                Country customerCountry = Country.builder()
                        .id(customerCountryId)
                        .name(customerCountryName)
                        .build();

                customer1.setCountry(customerCountry);
            }

            this.customer = customer1;
        }
        if (boatyardId != null) {
            Boatyard boatyard1 = Boatyard.builder().id(boatyardId).boatyardName(boatyardName).boatyardId(boatyardNumber).address(boatyardAddress).build();

            if (boatyardStateId != null) {
                State boatyardState = State.builder()
                        .id(boatyardStateId)
                        .name(boatyardStateName)
                        .build();

                boatyard1.setState(boatyardState);
            }

            if (boatyardCountryId != null) {
                Country boatyardCountry = Country.builder()
                        .id(boatyardCountryId)
                        .name(boatyardCountryName)
                        .build();

                boatyard1.setCountry(boatyardCountry);
            }

            this.boatyard = boatyard1;
        }
        if (technicianUserId != null)
            this.technicianUser = User.builder().id(technicianUserId).firstName(technicianUserFirstName).lastName(technicianUserLastName).build();
        if (customerOwnerUserId != null)
            this.customerOwnerUser = User.builder().id(customerOwnerUserId).firstName(customerOwnerUserFirstName).lastName(customerOwnerUserLastName).build();
        if (workOrderStatusId != null)
            this.workOrderStatus = WorkOrderStatus.builder().id(workOrderStatusId).status(workOrderStatusName).build();
        if (workOrderPayStatusId != null)
            this.workOrderPayStatus = WorkOrderPayStatus.builder().id(workOrderPayStatusId).status(workOrderPayStatusName).build();
        if (workOrderInvoiceId != null)
            this.workOrderInvoice = WorkOrderInvoice.builder().id(workOrderInvoiceId).build();
    }

    public WorkOrder(Integer id, String workOrderNumber, Date dueDate, Date scheduledDate,
                     Date completedDate, Time time, String problem,
                     Integer mooringId, String mooringNumber, String harborOrArea, String gpsCoordinates,
                     Date installBottomChainDate, Date installTopChainDate, Date installConditionOfEyeDate,
                     Date inspectionDate, String boatId, String boatName, String boatSize,
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
                     Integer customerId, String customerFirstName, String customerLastName, String customerNumber, String customerPhoneNumber, String customerAddress,
                     Integer customerStateId, String customerStateName,
                     Integer customerCountryId, String customerCountryName,
                     Integer userId, String userFirstName, String userLastName,
                     Integer boatyardId, String boatyardNumber, String boatyardName,
                     Integer serviceAreaId, String serviceAreaName,
                     Integer technicianUserId, String technicianUserFirstName, String technicianUserLastName,
                     Integer customerOwnerUserId, String customerOwnerUserFirstName, String customerOwnerUserLastName,
                     Integer workOrderStatusId, String workOrderStatusName, Integer workOrderPayStatusId,
                     String workOrderPayStatusName, Integer workOrderInvoiceId
    ) {
        this.id = id;
        this.workOrderNumber = workOrderNumber;
        this.dueDate = dueDate;
        this.scheduledDate = scheduledDate;
        this.completedDate = completedDate;
        this.time = time;
        this.problem = problem;
        if (mooringId != null) this.mooring =
                Mooring.builder()
                        .id(mooringId)
                        .mooringNumber(mooringNumber)
                        .harborOrArea(harborOrArea)
                        .gpsCoordinates(gpsCoordinates)
                        .installBottomChainDate(installBottomChainDate)
                        .installTopChainDate(installTopChainDate)
                        .installConditionOfEyeDate(installConditionOfEyeDate)
                        .inspectionDate(inspectionDate)
                        .boatId(boatId)
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
                        .user(User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build())
                        .serviceArea(ServiceArea.builder().id(serviceAreaId).serviceAreaName(serviceAreaName).build())
                        .build();
        if (customerId != null)
            this.customer = Customer.builder().id(customerId).firstName(customerFirstName).lastName(customerLastName).customerId(customerNumber).build();
        if (boatyardId != null)
            this.boatyard = Boatyard.builder().id(boatyardId).boatyardId(boatyardNumber).boatyardName(boatyardName).build();
        if (technicianUserId != null)
            this.technicianUser = User.builder().id(technicianUserId).firstName(technicianUserFirstName).lastName(technicianUserLastName).build();
        if (customerOwnerUserId != null)
            this.customerOwnerUser = User.builder().id(customerOwnerUserId).firstName(customerOwnerUserFirstName).lastName(customerOwnerUserLastName).build();
        if (workOrderStatusId != null)
            this.workOrderStatus = WorkOrderStatus.builder().id(workOrderStatusId).status(workOrderStatusName).build();
        if (workOrderPayStatusId != null)
            this.workOrderPayStatus = WorkOrderPayStatus.builder().id(workOrderPayStatusId).status(workOrderPayStatusName).build();
        if (workOrderInvoiceId != null)
            this.workOrderInvoice = WorkOrderInvoice.builder().id(workOrderInvoiceId).build();
    }

    public WorkOrder(Integer id, String workOrderNumber, Date dueDate, Date scheduledDate,
                     Date completedDate, Time time, String problem,
                     Integer mooringId, String mooringNumber, String harborOrArea, String gpsCoordinates,
                     Date installBottomChainDate, Date installTopChainDate, Date installConditionOfEyeDate,
                     Date inspectionDate, String boatId, String boatName, String boatSize,
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
                     Integer customerId, String customerFirstName, String customerLastName, String customerNumber, String quickbookCustomerId,
                     Integer userId, String userFirstName, String userLastName,
                     Integer boatyardId, String boatyardNumber, String boatyardName,
                     Integer serviceAreaId, String serviceAreaName,
                     Integer technicianUserId, String technicianUserFirstName, String technicianUserLastName,
                     Integer customerOwnerUserId, String customerOwnerUserFirstName, String customerOwnerUserLastName,
                     Integer workOrderStatusId, String workOrderStatusName, Integer workOrderPayStatusId,
                     String workOrderPayStatusName, Integer workOrderInvoiceId
    ) {
        this.id = id;
        this.workOrderNumber = workOrderNumber;
        this.dueDate = dueDate;
        this.scheduledDate = scheduledDate;
        this.completedDate = completedDate;
        this.time = time;
        this.problem = problem;
        if (mooringId != null) this.mooring =
                Mooring.builder()
                        .id(mooringId)
                        .mooringNumber(mooringNumber)
                        .harborOrArea(harborOrArea)
                        .gpsCoordinates(gpsCoordinates)
                        .installBottomChainDate(installBottomChainDate)
                        .installTopChainDate(installTopChainDate)
                        .installConditionOfEyeDate(installConditionOfEyeDate)
                        .inspectionDate(inspectionDate)
                        .boatId(boatId)
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
                        .user(User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build())
                        .serviceArea(ServiceArea.builder().id(serviceAreaId).serviceAreaName(serviceAreaName).build())
                        .build();
        if (customerId != null)
            this.customer = Customer.builder().id(customerId).firstName(customerFirstName).lastName(customerLastName).customerId(customerNumber).build();
        if (boatyardId != null)
            this.boatyard = Boatyard.builder().id(boatyardId).boatyardId(boatyardNumber).boatyardName(boatyardName).build();
        if (technicianUserId != null)
            this.technicianUser = User.builder().id(technicianUserId).firstName(technicianUserFirstName).lastName(technicianUserLastName).build();
        if (customerOwnerUserId != null)
            this.customerOwnerUser = User.builder().id(customerOwnerUserId).firstName(customerOwnerUserFirstName).lastName(customerOwnerUserLastName).build();
        if (workOrderStatusId != null)
            this.workOrderStatus = WorkOrderStatus.builder().id(workOrderStatusId).status(workOrderStatusName).build();
        if (workOrderPayStatusId != null)
            this.workOrderPayStatus = WorkOrderPayStatus.builder().id(workOrderPayStatusId).status(workOrderPayStatusName).build();
        if (workOrderInvoiceId != null)
            this.workOrderInvoice = WorkOrderInvoice.builder().id(workOrderInvoiceId).build();
    }

    public WorkOrder(
            Integer id, String workOrderNumber, Date dueDate, Date scheduledDate, Time time, String problem,
            String mooringNumber,
            String customerFirstName, String customerLastName, String customerId,
            String workOrderStatusName,
            String technicianUserFirstName, String technicianUserLastName, String technicianUserEmail
    ) {
        this.id = id;
        this.workOrderNumber = workOrderNumber;
        this.dueDate = dueDate;
        this.scheduledDate = scheduledDate;
        this.time = time;
        this.problem = problem;
        if (mooringNumber != null) this.mooring = Mooring.builder()
                .mooringNumber(mooringNumber)
                .customer(Customer.builder().firstName(customerFirstName).lastName(customerLastName).customerId(customerId).build())
                .build();
        if (workOrderStatusName != null)
            this.workOrderStatus = WorkOrderStatus.builder().status(workOrderStatusName).build();
        if (technicianUserFirstName != null)
            this.technicianUser = User.builder().firstName(technicianUserFirstName).lastName(technicianUserLastName).email(technicianUserEmail).build();
    }

    public WorkOrder(Integer id, String workOrderNumber, Date dueDate, Date scheduledDate,
                     Date completedDate, Time time, String problem, BigDecimal cost,
                     Integer mooringId, String mooringNumber, String harborOrArea, String gpsCoordinates,
                     Date installBottomChainDate, Date installTopChainDate, Date installConditionOfEyeDate,
                     Date inspectionDate, String boatId, String boatName, String boatSize,
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
                     Integer customerId, String customerFirstName, String customerLastName, String customerNumber, String customerPhoneNumber, String customerAddress,
                     Integer customerStateId, String customerStateName,
                     Integer customerCountryId, String customerCountryName,
                     Integer userId, String userFirstName, String userLastName,
                     Integer boatyardId, String boatyardNumber, String boatyardName,
                     Integer serviceAreaId, String serviceAreaName,
                     Integer technicianUserId, String technicianUserFirstName, String technicianUserLastName,
                     Integer customerOwnerUserId, String customerOwnerUserFirstName, String customerOwnerUserLastName,
                     Integer workOrderStatusId, String workOrderStatusName, Integer workOrderPayStatusId,
                     String workOrderPayStatusName, Integer workOrderInvoiceId
    ) {
        this.id = id;
        this.workOrderNumber = workOrderNumber;
        this.dueDate = dueDate;
        this.scheduledDate = scheduledDate;
        this.completedDate = completedDate;
        this.cost = cost;
        this.time = time;
        this.problem = problem;
        if (mooringId != null) this.mooring =
                Mooring.builder()
                        .id(mooringId)
                        .mooringNumber(mooringNumber)
                        .harborOrArea(harborOrArea)
                        .gpsCoordinates(gpsCoordinates)
                        .installBottomChainDate(installBottomChainDate)
                        .installTopChainDate(installTopChainDate)
                        .installConditionOfEyeDate(installConditionOfEyeDate)
                        .inspectionDate(inspectionDate)
                        .boatId(boatId)
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
                        .user(User.builder().id(userId).firstName(userFirstName).lastName(userLastName).build())
                        .serviceArea(ServiceArea.builder().id(serviceAreaId).serviceAreaName(serviceAreaName).build())
                        .build();
        if (customerId != null) {
            Customer customer1 = Customer.builder().id(customerId).firstName(customerFirstName).lastName(customerLastName).customerId(customerNumber).phone(customerPhoneNumber).address(customerAddress).build();

            if (customerStateId != null) {
                State customerState = State.builder()
                        .id(customerStateId)
                        .name(customerStateName)
                        .build();

                customer.setState(customerState);
            }

            if (customerStateId != null) {
                Country customerCountry = Country.builder()
                        .id(customerCountryId)
                        .name(customerCountryName)
                        .build();

                customer.setCountry(customerCountry);
            }

            this.customer = customer1;
        }
        if (boatyardId != null)
            this.boatyard = Boatyard.builder().id(boatyardId).boatyardId(boatyardNumber).boatyardName(boatyardName).build();
        if (technicianUserId != null)
            this.technicianUser = User.builder().id(technicianUserId).firstName(technicianUserFirstName).lastName(technicianUserLastName).build();
        if (customerOwnerUserId != null)
            this.customerOwnerUser = User.builder().id(customerOwnerUserId).firstName(customerOwnerUserFirstName).lastName(customerOwnerUserLastName).build();
        if (workOrderStatusId != null)
            this.workOrderStatus = WorkOrderStatus.builder().id(workOrderStatusId).status(workOrderStatusName).build();
        if (workOrderPayStatusId != null)
            this.workOrderPayStatus = WorkOrderPayStatus.builder().id(workOrderPayStatusId).status(workOrderPayStatusName).build();
        if (workOrderInvoiceId != null)
            this.workOrderInvoice = WorkOrderInvoice.builder().id(workOrderInvoiceId).build();
    }

}
