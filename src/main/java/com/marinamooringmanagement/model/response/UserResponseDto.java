package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.entity.Role;
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

    private String userID;

    private String customerAdminId;

    /**
     * The role associated with the user.
     */
    private String role;

    private String state;

    private String country;
}
