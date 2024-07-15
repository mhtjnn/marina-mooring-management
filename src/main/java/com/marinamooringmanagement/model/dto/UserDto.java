package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.dto.metadata.CountryDto;
import com.marinamooringmanagement.model.dto.metadata.StateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO (Data Transfer Object) class representing User entity data.
 * This class extends BaseDto and includes user-specific attributes.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 550268063035507976L;

    /**
     * The unique identifier for the user.
     */
    private Integer id;

    /**
     * The first name of the user.
     */
    private String name;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The phone number of the user.
     */
    private String phoneNumber;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * The ID of the customer admin associated with the user.
     */
    private Integer customerOwnerId;

    /**
     * The role associated with the user.
     */
    private RoleDto role;

    /**
     * The state associated with the user.
     */
    private StateDto stateDto;

    /**
     * The country associated with the user.
     */
    private CountryDto countryDto;

    /**
     * The street address of the user.
     */
    private String street;

    /**
     * The apartment number of the user.
     */
    private String apt;

    /**
     * The zip code of the user's location.
     */
    private String zipCode;
}



