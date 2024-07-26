package com.marinamooringmanagement.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @NotNull(message = "First name cannot be blank")
    private String firstName;

    @NotNull(message = "Last name cannot be blank")
    private String lastName;

    /**
     * The email address of the user.
     */
    @NotNull(message = "Email cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email address format.")
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
     * The role associated with the user.
     */
    private Integer roleId;

    private Integer customerOwnerId;

    private String companyName;

    /**
     * The state associated with the user.
     */
    private Integer stateId;

    /**
     * The country associated with the user.
     */
    private Integer countryId;

    /**
     * The street address associated with the user.
     */
    private String address;

    /**
     * The ZIP code associated with the user.
     */
    private String zipCode;

    private String confirmPassword;
}


