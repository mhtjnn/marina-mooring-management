package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
     * The apartment number associated with the BoatYard.
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
     * The ZIP code associated with the BoatYard.
     */
    private String zipCode;

    /**
     * The main contact detail for reaching the primary contact person associated with this BoatYard.
     */
    private String mainContact;

    /**
     * List of mooring response DTOs associated with the BoatYard.
     */
    private List<MooringResponseDto> mooringResponseDtoList;
}

