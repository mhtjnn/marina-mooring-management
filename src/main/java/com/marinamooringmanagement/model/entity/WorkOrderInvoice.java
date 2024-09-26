package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marinamooringmanagement.model.entity.metadata.WorkOrderInvoiceStatus;
import com.marinamooringmanagement.model.entity.metadata.WorkOrderPayStatus;
import com.marinamooringmanagement.model.entity.metadata.WorkOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "work_order_invoice")
public class WorkOrderInvoice extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "invoiceAmount")
    private Double invoiceAmount;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "work_order_invoice_status_id")
    private WorkOrderInvoiceStatus workOrderInvoiceStatus;

    @OneToOne
    @JoinColumn(name = "work_order_id")
    @JsonBackReference
    @ToString.Exclude
    private WorkOrder workOrder;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_owner_user_id")
    @ToString.Exclude
    private User customerOwnerUser;

    @OneToMany(mappedBy = "workOrderInvoice", cascade = {}, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<Payment> paymentList;

    public WorkOrderInvoice(Integer id, Double invoiceAmount, Integer workOrderInvoiceStatusId, String workOrderInvoiceStatusName,
                            Integer workOrderId, String workOrderNumber, Date dueDate, Date scheduledDate,
                            Date completedDate, Date creationDate ,Time time, String problem, Integer mooringId, String mooringNumber,
                            Integer technicianUserId, String technicianUserFirstName, String technicianUserLastName,
                            Integer woCustomerOwnerUserId, String woCustomerOwnerUserFirstName, String woCustomerOwnerUserLastName,
                            Integer workOrderStatusId, String workOrderStatusName,
                            Integer workOrderPayStatusId, String workOrderPayStatusName,
                            Integer customerId, String customerFirstName, String customerLastName,
                            String customerNumber, Integer boatyardId, String boatyardName,
                            Integer customerOwnerUserId,
                            String customerOwnerUserFirstName, String customerOwnerUserLastName
                            ) {
        this.id = id;
        this.invoiceAmount = invoiceAmount;
        this.workOrderInvoiceStatus = WorkOrderInvoiceStatus.builder().id(workOrderInvoiceStatusId).status(workOrderInvoiceStatusName).build();
        this.creationDate = creationDate;
        this.workOrder = WorkOrder.builder()
                .id(workOrderId)
                .workOrderNumber(workOrderNumber)
                .dueDate(dueDate)
                .scheduledDate(scheduledDate)
                .completedDate(completedDate)
                .time(time)
                .problem(problem)
                .mooring(Mooring.builder().id(mooringId).mooringNumber(mooringNumber)
                .customer(Customer.builder().id(customerId).firstName(customerFirstName).lastName(customerLastName).customerId(customerNumber).build())
                .boatyard(Boatyard.builder().id(boatyardId).boatyardName(boatyardName).build())
                .build())
                .technicianUser(User.builder().id(technicianUserId).firstName(technicianUserFirstName).lastName(technicianUserLastName).build())
                .customerOwnerUser(User.builder().id(woCustomerOwnerUserId).firstName(woCustomerOwnerUserFirstName).lastName(woCustomerOwnerUserLastName).build())
                .workOrderStatus(WorkOrderStatus.builder().id(workOrderStatusId).status(workOrderStatusName).build())
                .workOrderPayStatus(WorkOrderPayStatus.builder().id(workOrderPayStatusId).status(workOrderPayStatusName).build())
                .build();
        this.customerOwnerUser = User.builder().id(customerOwnerUserId).firstName(customerOwnerUserFirstName).lastName(customerOwnerUserLastName).build();
    }

}
