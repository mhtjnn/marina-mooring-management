package com.marinamooringmanagement.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a technician request, encapsulating details
 * necessary for managing technician information and requests within the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianRequestDto {
    /**
     * The unique identifier for the technician request.
     */
    private Integer id;
    /**
     * The name of the technician.
     */
    private String technicianName;
    /**
     * The identification number or code of the technician.
     */
    private String technicianId;
    /**
     * The email address of the technician.
     */
    private String emailAddress;
    /**
     * The phone number of the technician.
     */
    private String phone;
    /**
     * The street and house details of the technician's address.
     */
    private String streetHouse;
    /**
     * The sector or block of the technician's address.
     */
    private String sectorBlock;
    /**
     * The state or region of the technician's address.
     */
    private String state;
    /**
     * The country of the technician's address.
     */
    private String country;
    /**
     * The pin code or postal code of the technician's address.
     */
    private String pincode;
    /**
     * Additional notes or comments related to the technician request.
     */
    private String note;
}
