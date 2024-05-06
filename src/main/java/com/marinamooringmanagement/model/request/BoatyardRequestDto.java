package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Data Transfer Object (DTO) representing a request to or from a boatyard,
 * encapsulating details necessary for managing boatyard requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoatyardRequestDto {

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
     * The email address of the BoatYard owner.
     */
    private String emailAddress;

    /**
     * The phone number of the BoatYard.
     */
    private String phone;

    /**
     * The address associated with an entity, such as a customer or location.
     */
    private String street;

    /**
     * The apartment number of the BoatYard.
     */
    private String apt;

    /**
     * The state associated with the BoatYard.
     */
    private String state;

    /**
     * The country associated with the BoatYard.
     */
    private String country;

    /**
     * The zip code of the BoatYard.
     */
    private String zipCode;

    /**
     * The main contact detail, typically a name or a primary phone number,
     * for reaching the primary contact person associated with this entity.
     */
    private String mainContact;

    private String gpsCoordinates;
}

