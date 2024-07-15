package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.metadata.WorkOrderStatusDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstimateResponseDto {

    private Integer id;

    private String dueDate;

    private String scheduledDate;

    private Time time;

    private String problem;

    private CustomerResponseDto customerResponseDto;

    private MooringResponseDto mooringResponseDto;

    private BoatyardResponseDto boatyardResponseDto;

    private UserResponseDto technicianUserResponseDto;

    private UserResponseDto customerOwnerUserResponseDto;

    private WorkOrderStatusDto workOrderStatusDto;

}
