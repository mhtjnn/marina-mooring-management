package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.metadata.ServiceAreaTypeDto;
import com.marinamooringmanagement.model.response.metadata.CountryResponseDto;
import com.marinamooringmanagement.model.response.metadata.StateResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAreaResponseDto {
    private Integer id;

    private String serviceAreaName;

    private ServiceAreaTypeDto serviceAreaTypeDto;

    private String streetHouse;

    private String aptSuite;

    private StateResponseDto stateResponseDto;

    private CountryResponseDto countryResponseDto;

    private String gpsCoordinates;

    private String notes;

    private Integer userId;

    private Integer mooringInventoried;
}
