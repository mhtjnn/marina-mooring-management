package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.metadata.PaymentTypeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {

    private Integer id;

    private PaymentTypeDto paymentTypeDto;

    private Double amount;

    private UserResponseDto customerOwnerUserResponseDto;

    private WorkOrderInvoiceResponseDto workOrderInvoiceResponseDto;

}
