package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.entity.Mooring;
import com.marinamooringmanagement.model.entity.Technician;
import com.marinamooringmanagement.model.entity.WorkOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderDto extends BaseDto{

    private Integer id;

    private Mooring mooring;

    private TechnicianDto technicianDto;

    private Date dueDate;

    private String problem;

    private WorkOrderStatusDto workOrderStatusDto;

}
