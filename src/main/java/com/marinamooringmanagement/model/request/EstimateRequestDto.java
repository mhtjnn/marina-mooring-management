package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstimateRequestDto {

    private Integer id;

    private Date dueDate;

    private Date scheduledDate;

    private Time time;

    private String problem;

    private Integer customerId;

    private Integer mooringId;

    private Integer boatyardId;

    private Integer technicianId;

    private Integer workOrderStatusId;

}
