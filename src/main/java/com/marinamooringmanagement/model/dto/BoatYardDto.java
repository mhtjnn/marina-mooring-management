package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Data Transfer Object (DTO) for BoatYard entities.
 * This DTO represents a BoatYard and is used for transferring data between layers of the application.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BoatYardDto {
    /**
     * The unique identifier of the BoatYard.
     */
    private Integer id;
    /**
     * The boatyard ID of the BoatYard.
     */
    private String boatyardId;
    /**
     * The mooring name of the BoatYard.
     */
    private String mooringName;
    /**
     * The owner name of the BoatYard.
     */
    private String ownerName;
    /**
     * The email address of the BoatYard owner.
     */
    private String emailAddress;
    /**
     * The phone number of the BoatYard.
     */
    private String phone;

}

