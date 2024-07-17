package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAreaRequestDto {

    private Integer id;

    private String serviceAreaName;

    private Integer serviceAreaTypeId;

    private String streetHouse;

    private String aptSuite;

    private Integer stateId;

    private Integer countryId;

    private String notes;

    private String gpsCoordinates;

}
