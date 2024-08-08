package com.marinamooringmanagement.repositories;

import com.marinamooringmanagement.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Query("SELECT new com.marinamooringmanagement.model.entity.Payment(" +
            "p.id, pt.id, pt.type, p.amount, " +
            "u.id, u.firstName, u.lastName, " +
            "woi.id) " +
            "FROM Payment p " +
            "JOIN p.paymentType pt " +
            "JOIN p.customerOwnerUser u " +
            "JOIN p.workOrderInvoice woi " +
            "WHERE woi.id = :workOrderInvoiceId AND u.id = :userId")
    List<Payment> findPaymentsByWorkOrderInvoice(@Param("workOrderInvoiceId") Integer workOrderInvoiceId,
                                                 @Param("userId") Integer userId);

}
