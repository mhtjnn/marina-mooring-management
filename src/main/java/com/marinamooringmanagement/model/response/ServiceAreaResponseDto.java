package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.metadata.CountryDto;
import com.marinamooringmanagement.model.dto.metadata.ServiceAreaTypeDto;
import com.marinamooringmanagement.model.dto.metadata.StateDto;
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

    private StateDto stateDto;

    private CountryDto countryDto;

    private String notes;
}
