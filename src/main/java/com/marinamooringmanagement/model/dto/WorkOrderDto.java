package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.entity.WorkOrderPayStatus;
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

    private Date completedDate;

    private Time time;

    private String problem;

    private WorkOrderStatusDto workOrderStatusDto;

    private WorkOrderPayStatusDto workOrderPayStatusDto;

    private MooringDto mooringDto;

    private UserDto customerOwnerUserDto;

    private UserDto technicianUserDto;

    private UserDto financeUserDto;

    private List<ImageDto> imageDtoList;
}
