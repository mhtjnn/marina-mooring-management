package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.dto.metadata.PaymentTypeDto;
import com.marinamooringmanagement.model.dto.metadata.WorkOrderInvoiceStatusDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderInvoiceDto extends BaseDto{

    private Integer id;

    private BigDecimal invoiceAmount;

    private WorkOrderInvoiceStatusDto workOrderInvoiceStatusDto;

    private WorkOrderDto workOrderDto;

    private List<PaymentDto> paymentDtoList;
}
