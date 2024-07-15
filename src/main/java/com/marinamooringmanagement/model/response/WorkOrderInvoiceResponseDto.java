package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.metadata.WorkOrderInvoiceStatusDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderInvoiceResponseDto {

    private Integer id;

    private String invoiceDate;

    private String lastContactTime;

    private Double invoiceAmount;

    private WorkOrderInvoiceStatusDto workOrderInvoiceStatusDto;

    private WorkOrderResponseDto workOrderResponseDto;

}
