package com.marinamooringmanagement.security.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Class representing an Authentication Request containing username and password.
 * This class is used to encapsulate user credentials for authentication purposes.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest implements Serializable {

    private static final long serialVersionUID = 550269063035507976L;

    /**
     * The username used for authentication.
     */
    @NotNull(message = "Username cannot be null")
    private String username;

    /**
     * The password used for authentication.
     */
    @NotNull(message = "Password cannot be null")
    private String password;
}
