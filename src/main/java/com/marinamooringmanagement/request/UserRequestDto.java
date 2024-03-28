package com.marinamooringmanagement.request;

import com.marinamooringmanagement.model.dto.BaseDto;
import com.marinamooringmanagement.model.dto.RoleDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data transfer object (DTO) for user requests.
 * This class represents the data structure used for creating or updating user information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto implements Serializable {
    /**
     * The unique identifier for the user.
     */
    private Integer id;

    /**
     * The first name of the user.
     */
    @NotNull(message = "First Name can't be blank")
    private String firstname;

    /**
     * The last name of the user.
     */
    private String lastname;

    /**
     * The email address of the user.
     */
    @Email(message = "Email is not Valid")
    private String email;

    /**
     * The phone number of the user.
     */
    private String phoneNumber;

    /**
     * The password for the user account.
     */
    @NotEmpty(message = "Password can't be blank")
    private String password;

    /**
     * The role ID associated with the user.
     */
    private Integer roleId;
}
