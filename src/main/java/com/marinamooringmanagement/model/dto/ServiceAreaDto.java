package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.dto.metadata.CountryDto;
import com.marinamooringmanagement.model.dto.metadata.ServiceAreaTypeDto;
import com.marinamooringmanagement.model.dto.metadata.StateDto;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.entity.metadata.Country;
import com.marinamooringmanagement.model.entity.metadata.ServiceAreaType;
import com.marinamooringmanagement.model.entity.metadata.State;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAreaDto extends BaseDto{

    private Integer id;

    private String serviceAreaName;

    private ServiceAreaTypeDto serviceAreaTypeDto;

    private String address;

    private StateDto stateDto;

    private CountryDto countryDto;

    private String zipCode;

    private String gpsCoordinates;

    private List<String> subServiceAreaList;

    private UserDto userDto;

    private String notes;
}
