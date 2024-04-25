package com.marinamooringmanagement.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
public class BoatYardDto extends BaseDto {
    /**
     * The unique identifier of the BoatYard.
     */
    private Integer id;
    /**
     * The boatyard ID of the BoatYard.
     */
    private String boatyardId;
    /**
     * The name of the BoatYard.
     */
    @NotNull(message = "BoatYard Name can't be blank")
    private String boatYardName;

    /**
     * The email address of the BoatYard owner.
     */
    @Email(message = "Email is not Valid")
    private String emailAddress;
    /**
     * The phone number of the BoatYard.
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

