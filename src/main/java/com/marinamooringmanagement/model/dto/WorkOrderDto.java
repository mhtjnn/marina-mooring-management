package com.marinamooringmanagement.model.dto;

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
public class WorkOrderDto extends BaseDto{

    private Integer id;

    private Date dueDate;

    private Date scheduledDate;

    private Time time;

    private String problem;

    private WorkOrderStatusDto workOrderStatusDto;

    private MooringDto mooringDto;

    private UserDto customerOwnerUserDto;

    private UserDto technicianUserDto;

    private List<ImageDto> imageDtoList;
}
