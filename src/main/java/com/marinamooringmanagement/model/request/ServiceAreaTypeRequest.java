package com.marinamooringmanagement.model.request;

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
public class ServiceAreaTypeRequest {

    private Integer id;

    private String serviceAreaName;

    private Integer serviceAreaTypeId;

    private String streetHouse;

    private String aptSuite;

    private Integer stateId;

    private Integer countryId;

    private String notes;

}
