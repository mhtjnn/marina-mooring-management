package com.marinamooringmanagement.model.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.marinamooringmanagement.model.entity.Customer;
import com.marinamooringmanagement.model.entity.Mooring;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.entity.WorkOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto extends BaseDto{

    private Integer id;

    private String imageName;

    private String note;

    private byte[] imageData;

    private MooringDto mooringDto;

    private CustomerDto customerDto;

    private WorkOrderDto workOrderDto;

    private UserDto userDto;

    private UserDto customerOwnerUserDto;
}
