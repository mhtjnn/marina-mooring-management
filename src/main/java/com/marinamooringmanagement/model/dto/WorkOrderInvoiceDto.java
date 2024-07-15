package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderInvoiceDto extends BaseDto{

    private Integer id;

    private Double invoiceAmount;

    private WorkOrderInvoiceStatusDto workOrderInvoiceStatusDto;

    private WorkOrderDto workOrderDto;
}
