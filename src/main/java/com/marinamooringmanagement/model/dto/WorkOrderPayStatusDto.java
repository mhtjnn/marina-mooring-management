package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderPayStatusDto extends BaseDto{

    private Integer id;

    private String status;

    private String description;
}
