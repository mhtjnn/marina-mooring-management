package com.marinamooringmanagement.model.response;

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
public class WorkOrderResponseDto {

    private Integer id;

    private Date dueDate;

    private Date scheduledDate;

    private Time time;

    private String problem;

    private CustomerResponseDto customerResponseDto;

    private MooringResponseDto mooringResponseDto;

    private BoatyardResponseDto boatyardResponseDto;

    private UserResponseDto technicianUserResponseDto;

    private UserResponseDto customerOwnerUserResponseDto;
}
