package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstimateRequestDto {

    private Integer id;

    private String dueDate;

    private String scheduledDate;

    private Time time;

    private String problem;

    private BigDecimal cost;

    private Integer customerId;

    private Integer mooringId;

    private Integer boatyardId;

    private Integer technicianId;

    private Integer workOrderStatusId;

}
