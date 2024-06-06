package com.marinamooringmanagement.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
     * The email address of the BoatYard owner.
     */
    @NotNull(message = "Email cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email address format.")
    private String emailAddress;

    /**
     * The phone number of the BoatYard.
     */
    @Pattern(regexp = "^\\+\\d{1} \\d{3} \\d{3} \\d{4}$", message = "Invalid phone number format.")
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

    @Pattern(regexp = "^([-+]?[1-8]?\\d(.\\d+)?|90(.0+)?)\\s+([-+]?(1[0-7]\\d(.\\d+)?|[1-9]?\\d(.\\d+)?|180(.0+)?))$", message = "Invalid GPS coordinates format.")
    private String gpsCoordinates;
}

