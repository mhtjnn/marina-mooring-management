package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianRequestDto {
    private Integer id;
    private String technicianName;
    private String technicianId;
    private String emailAddress;
    private String phone;
    private String streetHouse;
    private String sectorBlock;
    private String state;
    private String country;
    private String pincode;
    private String note;
}
