package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoiceMEMODto extends BaseDto{

    private Integer id;

    private String name;

    private byte[] data;

    private WorkOrderDto workOrderDto;

    private UserDto userDto;

}
