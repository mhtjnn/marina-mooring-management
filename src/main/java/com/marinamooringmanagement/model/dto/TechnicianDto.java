package com.marinamooringmanagement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Data Transfer Object (DTO) class representing a Technician.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianDto {

    private Integer id;
    /**
     * The name of the technician.
     */
    private String technicianName;
    /**
     * The ID of the technician.
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
     * The street and house number of the technician's address.
     */
    private String streetHouse;
    /**
     * The sector or block of the technician's address.
     */
    private String sectorBlock;
    /**
     * The state of the technician's address.
     */
    private String state;
    /**
     * The country of the technician's address.
     */
    private String country;
    /**
     * The pin code of the technician's address.
     */
    private String pincode;
    /**
     * Additional notes or comments about the technician.
     */
    private String note;
}
