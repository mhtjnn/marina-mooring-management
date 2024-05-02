package com.marinamooringmanagement.model.dto;

import com.marinamooringmanagement.model.entity.Role;
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

    private String userID;

    private String customerAdminId;

    /**
     * The role associated with the user.
     */
    private RoleDto role;

    private StateDto stateDto;

    private CountryDto countryDto;
}

