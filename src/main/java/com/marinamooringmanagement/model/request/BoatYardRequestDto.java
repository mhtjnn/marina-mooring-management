package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoatYardRequestDto {
    private Integer id;
    private String boatyardId;
    private String mooringName;
    private String ownerName;
    private String emailAddress;
    private String phone;
}
