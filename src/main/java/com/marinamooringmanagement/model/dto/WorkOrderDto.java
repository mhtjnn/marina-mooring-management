package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.dto.metadata.WorkOrderPayStatusDto;
import com.marinamooringmanagement.model.dto.metadata.WorkOrderStatusDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderDto extends BaseDto{

    private Integer id;

    private String workOrderNumber;

    private Date dueDate;

    private Date scheduledDate;

    private Date completedDate;

    private Time time;

    private String problem;

    private String reasonForDenial;

    private BigDecimal cost;

    private WorkOrderStatusDto workOrderStatusDto;

    private WorkOrderPayStatusDto workOrderPayStatusDto;

    private MooringDto mooringDto;

    private CustomerDto customerDto;

    private BoatyardDto boatyardDto;

    private UserDto customerOwnerUserDto;

    private UserDto technicianUserDto;

    private UserDto financeUserDto;

    private List<ImageDto> imageDtoList;

    private List<FormDto> formDtoList;

    private List<InventoryDto> inventoryDtoList;
}
