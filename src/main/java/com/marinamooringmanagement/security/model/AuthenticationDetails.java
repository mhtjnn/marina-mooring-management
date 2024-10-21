package com.marinamooringmanagement.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Class representing authentication details including loggedInUserId, loggedInUserEmail, and loggedInUserRole.
 * These details are typically used for user authentication and authorization purposes.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationDetails implements Serializable {

    private static final long serialVersionUID = 550269061132507976L;

    /**
     * The ID of the logged-in user.
     */
    private Integer loggedInUserId;
    /**
     * The email address of the logged-in user.
     */
    private String loggedInUserEmail;
    /**
     * The role of the logged-in user.
     */
    private String loggedInUserRole;
}
