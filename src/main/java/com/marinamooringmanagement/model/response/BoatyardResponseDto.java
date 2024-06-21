package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a response from a boatyard,
 * encapsulating details about the boatyard, mooring, and owner.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoatyardResponseDto {
    /**
     * The unique identifier of the BoatYard.
     */
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

    /**
     * The apartment number associated with the BoatYard.
     */
    private String apt;

    /**
     * The state associated with the BoatYard.
     */
    private StateResponseDto stateResponseDto;

    /**
     * The country associated with the BoatYard.
     */
    private CountryResponseDto countryResponseDto;

    /**
     * The ZIP code associated with the BoatYard.
     */
    private String zipCode;

    /**
     * The main contact detail for reaching the primary contact person associated with this BoatYard.
     */
    private String mainContact;

    private String gpsCoordinates;

    /**
     * List of mooring response DTOs associated with the BoatYard.
     */
    private Integer mooringInventoried;

    private Integer userId;
}

