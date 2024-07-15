package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.dto.metadata.CountryDto;
import com.marinamooringmanagement.model.dto.metadata.StateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * Data Transfer Object (DTO) for BoatYard entities.
 * This DTO represents a BoatYard and is used for transferring data between layers of the application.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoatyardDto extends BaseDto {

    private Integer id;

    /**
     * The boatyard ID of the BoatYard.
     */
    private String boatyardId;

    /**
     * The name of the BoatYard.
     */
    private String boatyardName;

    /**
     * The address associated with an entity, such as a customer or location.
     */
    private String street;

    private String apt;

    /**
     * The state associated with the BoatYard.
     */
    private StateDto state;

    /**
     * The country associated with the BoatYard.
     */
    private CountryDto country;

    private String zipCode;

    /**
     * The main contact detail, typically a name or a primary phone number,
     * for reaching the primary contact person associated with this entity.
     */
    private String mainContact;

    private String gpsCoordinates;

    private List<String> storageAreas;

    /**
     * List of moorings associated with the BoatYard.
     */
    private List<MooringDto> mooringDtoList;

    private UserDto user;
}

