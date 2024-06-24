package com.marinamooringmanagement.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * Data Transfer Object (DTO) representing a request to or from a boatyard,
 * encapsulating details necessary for managing boatyard requests.
 */
@Data
@Builder
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
    @NotNull(message = "Boatyard name cannot be null")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Invalid name format.")
    private String boatyardName;

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
    private Integer stateId;

    /**
     * The country associated with the BoatYard.
     */
    private Integer countryId;

    /**
     * The zip code of the BoatYard.
     */
    @Pattern(regexp = "^\\d{3,10}(-\\d{3,10})?$|^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$|^[A-Z]{1,2}\\d[A-Z\\d]? \\d[A-Z]{2}$", message = "Invalid zipcode format.")
    private String zipCode;

    /**
     * The main contact detail, typically a name or a primary phone number,
     * for reaching the primary contact person associated with this entity.
     */
    private String mainContact;

    private String gpsCoordinates;

    private List<String> storageAreas;
}

