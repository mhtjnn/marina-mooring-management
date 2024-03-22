package com.marinamooringmanagement.security.controller;

import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.security.config.JwtUtil;
import com.marinamooringmanagement.security.model.AuthenticationRequest;
import com.marinamooringmanagement.security.model.AuthenticationResponse;
import com.marinamooringmanagement.service.UserService;
import com.marinamooringmanagement.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller class for handling authentication-related endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    TokenService tokenService;

    /**
     * Endpoint to create an authentication token based on the provided credentials.
     *
     * @param authenticationRequest the authentication request containing username and password
     * @param request               the HTTP servlet request
     * @return a ResponseEntity containing the authentication response
     * @throws Exception if an error occurs during authentication
     */

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody AuthenticationRequest authenticationRequest,
            HttpServletRequest request
    ) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        return generateAuthenticationResponse(authenticationRequest.getUsername());
    }
    /**
     * Generates the authentication response containing the JWT token and user details.
     *
     * @param username the username for which to generate the authentication response
     * @return a ResponseEntity containing the authentication response
     */

    private ResponseEntity<?> generateAuthenticationResponse(String username) {
        final UserDto emp = userService.findByEmailAddress(username);
        final String token = jwtUtil.generateToken(emp);
        tokenService.saveToken(emp, token);
        final AuthenticationResponse response = new AuthenticationResponse();
        response.setToken(token);
        response.setUser(emp);
        response.setStatus(HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
