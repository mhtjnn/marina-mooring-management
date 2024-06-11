package com.marinamooringmanagement.security.model;

import com.marinamooringmanagement.model.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Class representing an Authentication Response containing token, user details, and status.
 * This class is used to encapsulate the response data after successful authentication.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse implements Serializable {

    private static final long serialVersionUID = 550269063035507976L;
    /**
     * The authentication token generated after successful authentication.
     */
    private String token;

    private String refreshToken;

    /**
     * The user details associated with the authenticated user.
     */
    private UserDto user;
    /**
     * The HTTP status code indicating the status of the authentication response.
     */
    private Integer status;
}
