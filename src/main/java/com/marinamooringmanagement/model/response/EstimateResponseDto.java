package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.metadata.WorkOrderStatusDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstimateResponseDto implements Serializable {

    private static final long serialVersionUID = 5526863507109L;

    private Integer id;

    private String dueDate;

    private String scheduledDate;

    private Time time;

    private String problem;

    private BigDecimal cost;

    private CustomerResponseDto customerResponseDto;

    private MooringResponseDto mooringResponseDto;

    private BoatyardResponseDto boatyardResponseDto;

    private UserResponseDto technicianUserResponseDto;

    private UserResponseDto customerOwnerUserResponseDto;

    private WorkOrderStatusDto workOrderStatusDto;

}
