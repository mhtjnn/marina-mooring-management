package com.marinamooringmanagement.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Data Transfer Object (DTO) class representing a Technician.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianDto extends BaseDto {

    private Integer id;
    /**
     * The name of the technician.
     */
    @NotNull(message = "Technician Name can't be blank")
    private String technicianName;
    /**
     * The ID of the technician.
     */
    private String technicianId;
    /**
     * The email address of the technician.
     */
    @Email(message = "Email is not Valid")
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
    @NotEmpty(message = "state can't be empty")
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
