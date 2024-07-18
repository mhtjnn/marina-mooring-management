package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuickbookCustomerDto extends BaseDto{

    private Integer id;

    private String customerName;

    private String customerId;

    private UserDto userDto;
}
