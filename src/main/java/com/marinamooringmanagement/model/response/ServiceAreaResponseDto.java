package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.metadata.ServiceAreaTypeDto;
import com.marinamooringmanagement.model.response.metadata.CountryResponseDto;
import com.marinamooringmanagement.model.response.metadata.StateResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAreaResponseDto implements Serializable {

    private static final long serialVersionUID = 5526863745079L;

    private Integer id;

    private String serviceAreaName;

    private ServiceAreaTypeDto serviceAreaTypeDto;

    private String address;

    private StateResponseDto stateResponseDto;

    private CountryResponseDto countryResponseDto;

    private String gpsCoordinates;

    private String zipCode;

    private String notes;

    private Integer userId;

    private List<String> subServiceAreas;

    private Integer mooringInventoried;
}
