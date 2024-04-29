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
public class BoatYardResponseDto {
    /**
     * The unique identifier for the boatyard response.
     */
    private Integer id;
    /**
     * The identifier of the boatyard associated with this response.
     */
    private String boatyardId;
    /**
     * The name linked to the boatyard.
     */
    private String boatYardName;

    /**
     * The email address of the boat owner.
     */
    private String emailAddress;
    /**
     * The phone number of the boat owner.
     */
    private String phone;
    /**
     * The address associated with an entity, such as a customer or location.
     */
    private String address;
    /**
     * The main contact detail, typically a name or a primary phone number,
     * for reaching the primary contact person associated with this entity.
     */
    private String mainContact;

}
