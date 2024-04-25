package com.marinamooringmanagement.model.response;


import lombok.*;

import java.io.Serializable;

/**
 * Response DTO (Data Transfer Object) class for {@link com.marinamooringmanagement.model.entity.User}.
 * This class is used for transferring user-related response data between layers of the application.
 */
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Data
@Builder
public class UserResponseDto implements Serializable {

    /**
     * The first name of the user.
     */
    private String firstname;

    /**
     * The last name of the user.
     */
    private String lastname;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The phone number of the user.
     */
    private String phoneNumber;

    /**
     * The role name of the user.
     */
    private String role;
}
