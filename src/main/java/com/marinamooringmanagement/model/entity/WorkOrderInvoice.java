package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marinamooringmanagement.model.entity.metadata.WorkOrderInvoiceStatus;
import jakarta.persistence.*;
import lombok.*;

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
    private User customerOwnerUser;

    @OneToMany(mappedBy = "workOrderInvoice", cascade = {}, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Payment> paymentList;
}
