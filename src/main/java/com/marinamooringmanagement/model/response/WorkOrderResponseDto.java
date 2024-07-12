package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.ImageDto;
import com.marinamooringmanagement.model.dto.WorkOrderPayStatusDto;
import com.marinamooringmanagement.model.dto.WorkOrderStatusDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderResponseDto {

    private Integer id;

    private String workOrderNumber;

    private String dueDate;

    private String scheduledDate;

    private String completedDate;

    private Time time;

    private String problem;

    private CustomerResponseDto customerResponseDto;

    private MooringResponseDto mooringResponseDto;

    private BoatyardResponseDto boatyardResponseDto;

    private UserResponseDto technicianUserResponseDto;

    private UserResponseDto financeUserResponseDto;

    private UserResponseDto customerOwnerUserResponseDto;

    private WorkOrderStatusDto workOrderStatusDto;

    private WorkOrderPayStatusDto workOrderPayStatusDto;

    private List<ImageDto> imageDtoList;
}
