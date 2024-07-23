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
    private String zipCode;

    /**
     * The main contact detail, typically a name or a primary phone number,
     * for reaching the primary contact person associated with this entity.
     */
    @NotNull(message = "main contact cannot be null")
    private String mainContact;

    private String gpsCoordinates;

    private List<String> storageAreas;
}

