package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.metadata.ServiceAreaTypeDto;
import com.marinamooringmanagement.model.response.metadata.CountryResponseDto;
import com.marinamooringmanagement.model.response.metadata.StateResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAreaResponseDto implements Serializable {

    private static final long serialVersionUID = 5526863745079L;

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
