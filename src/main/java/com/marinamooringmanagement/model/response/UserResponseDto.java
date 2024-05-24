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

    private static final long serialVersionUID = 1234534567890L;

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
     * The ID of the customer admin associated with the user.
     */
    private Integer customerAdminId;

    /**
     * The role associated with the user.
     */
    private String role;

    /**
     * The state associated with the user.
     */
    private String state;

    /**
     * The country associated with the user.
     */
    private String country;

    /**
     * The street address.
     */
    private String street;

    /**
     * The apartment or unit number.
     */
    private String apt;

    /**
     * The ZIP code.
     */
    private String zipCode;

    private String companyName;
}
