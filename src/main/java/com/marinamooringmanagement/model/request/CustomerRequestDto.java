package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequestDto {

    private Integer id;

    private String customerName;

    private String customerId;

    private String phone;

    private String emailAddress;

    private String streetHouse;

    private String sectorBlock;

    private String state;

    private String country;

    private String pinCode;

    private String note;

}
