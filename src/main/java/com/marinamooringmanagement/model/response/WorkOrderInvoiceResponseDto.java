package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.metadata.WorkOrderInvoiceStatusDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderInvoiceResponseDto implements Serializable {

    private static final long serialVersionUID = 5526863215079L;

    private Integer id;

    private String invoiceDate;

    private String lastContactTime;

    private Double invoiceAmount;

    private WorkOrderInvoiceStatusDto workOrderInvoiceStatusDto;

    private WorkOrderResponseDto workOrderResponseDto;

    private List<PaymentResponseDto> paymentResponseDtoList;

}
