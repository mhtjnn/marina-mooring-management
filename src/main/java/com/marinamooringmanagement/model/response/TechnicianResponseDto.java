package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TechnicianResponseDto {
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
