package com.marinamooringmanagement.model.request;

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
    @NotNull(message = "First name cannot be null")
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
    private String role;

    private String state;

    private String country;
}
