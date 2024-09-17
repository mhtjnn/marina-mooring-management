package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormDto extends BaseDto{

    private Integer id;

    private String formName;

    private String fileName;

    private byte[] formData;

    private UserDto userDto;

    private WorkOrderDto workOrderDto;

    private Integer parentFormId;
}
