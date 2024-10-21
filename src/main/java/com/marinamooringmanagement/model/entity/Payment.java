package com.marinamooringmanagement.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.marinamooringmanagement.model.entity.metadata.PaymentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment")
public class Payment extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_type_id")
    private PaymentType paymentType;

    @Column(name = "amount")
    private Double amount;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_owner_id")
    private User customerOwnerUser;

    @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_invoice_id")
    @JsonBackReference
    @ToString.Exclude
    private WorkOrderInvoice workOrderInvoice;

    public Payment(
            Integer id, Integer paymentTypeId, String paymentTypeName, Double amount,
            Integer customerOwnerUserId, String customerOwnerUserFirstName,
            String customerOwnerUserLastName, Integer workOrderInvoiceId
    ) {
        this.id = id;
        this.paymentType = PaymentType.builder().id(paymentTypeId).type(paymentTypeName).build();
        this.amount = amount;
        this.customerOwnerUser = User.builder()
                .id(customerOwnerUserId)
                .firstName(customerOwnerUserFirstName)
                .lastName(customerOwnerUserLastName)
                .build();
        this.workOrderInvoice = WorkOrderInvoice.builder().id(workOrderInvoiceId).build();
    }
}
