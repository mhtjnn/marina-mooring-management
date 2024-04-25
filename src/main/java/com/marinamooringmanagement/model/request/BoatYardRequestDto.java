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
public class BoatYardRequestDto {
    /**
     * The unique identifier for the boatyard request.
     */
    private Integer id;
    /**
     * The identifier of the specific boatyard related to this request.
     * This can be used to locate and reference the boatyard within the system.
     */
    private String boatyardId;
    /**
     * The name or designation of the boatyard location associated with this request.
     * Useful for identifying the specific mooring spot at the boatyard.
     */
    private String boatYardName;

    /**
     * The email address of the boat owner, used as a primary method of communication
     * regarding the request and updates related to boatyard services.
     */
    private String emailAddress;
    /**
     * The phone number of the boat owner, providing a direct line of contact.
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
