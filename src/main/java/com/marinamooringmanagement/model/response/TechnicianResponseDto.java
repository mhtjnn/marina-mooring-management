package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a response for technician details,
 * encapsulating information such as technician name, contact details, and address.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TechnicianResponseDto {
    /**
     * The unique identifier for the technician response.
     */
    private Integer id;
    /**
     * The name of the technician.
     */
    private String technicianName;
    /**
     * The identification number of the technician.
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
     * The pin code of the technician's address.
     */
    private String zipcode;
    /**
     * Additional notes related to the technician response.
     */
    private String note;
}
