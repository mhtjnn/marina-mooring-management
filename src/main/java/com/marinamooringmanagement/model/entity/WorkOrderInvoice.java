package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.marinamooringmanagement.model.entity.metadata.WorkOrderInvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JsonBackReference
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_owner_user_id")
    private User customerOwnerUser;
}
