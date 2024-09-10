package com.marinamooringmanagement.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAreaRequestDto {

    private Integer id;

    @NotNull(message = "Boatyard name cannot be null")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Invalid name format.")
    private String serviceAreaName;

    private Integer serviceAreaTypeId;

    private String address;

    private Integer stateId;

    private Integer countryId;

    private String zipCode;

    private String notes;

    private String gpsCoordinates;

    private List<String> subServiceAreaList;

}
