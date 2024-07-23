package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.dto.metadata.PaymentTypeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto extends BaseDto{

    private Integer id;

    private PaymentTypeDto paymentTypeDto;

    private Double amount;

    private UserDto customerOwnerUserDto;

    private WorkOrderInvoiceDto workOrderInvoiceDto;

}
