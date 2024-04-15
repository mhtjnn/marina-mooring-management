package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoatYardResponseDto {
    private Integer id;
    private String boatyardId;
    private String mooringName;
    private String ownerName;
    private String emailAddress;
    private String phone;

}
