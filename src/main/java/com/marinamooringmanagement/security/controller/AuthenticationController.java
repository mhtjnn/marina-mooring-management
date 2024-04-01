package com.marinamooringmanagement.security.controller;

import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.model.request.NewPasswordRequest;
import com.marinamooringmanagement.model.response.EmailLinkResponse;
import com.marinamooringmanagement.model.response.NewPasswordResponse;
import com.marinamooringmanagement.security.config.JwtUtil;
import com.marinamooringmanagement.security.model.AuthenticationRequest;
import com.marinamooringmanagement.security.model.AuthenticationResponse;
import com.marinamooringmanagement.model.request.ForgetPasswordEmailRequest;
import com.marinamooringmanagement.service.EmailService;
import com.marinamooringmanagement.service.UserService;
import com.marinamooringmanagement.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * Controller class for handling authentication-related endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

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
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );
            return generateAuthenticationResponse(authenticationRequest.getUsername());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Bad Credentials!!!");
        }
    }

    /**
     * Function to send Link through email for password reset functionality.
     * @param request the HTTP servlet request
     * @param forgetPasswordEmailRequest Request containing the email where the reset password link has to be sent.
     * @return a ResponseEntity containing the response
     * @throws Exception
     */
    @RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
    public ResponseEntity<?> forgetPassword(
            HttpServletRequest request,
            @Valid @RequestBody ForgetPasswordEmailRequest forgetPasswordEmailRequest) throws Exception {
        EmailLinkResponse response = emailService.sendMail(request, forgetPasswordEmailRequest);
        return response.isSuccess() ? new ResponseEntity(response.getResponse(), HttpStatus.OK) : new ResponseEntity(response.getResponse(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Function to validate email(existence) and token.
     * @param token The Reset Password JWT
     * @return  a ResponseEntity containing the response from EmailLinkResponse
     * @throws Exception
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
    public ResponseEntity<?> validateEmailAndToken(
            @RequestParam("token") String token) throws Exception {
        EmailLinkResponse response = userService.checkEmailAndTokenValid(token);
        return response.isSuccess() ? new ResponseEntity(response.getResponse(), HttpStatus.OK) : new ResponseEntity(response.getResponse(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Function to reset password with the new password.
     * @param token The reset password JWT
     * @param newPasswordRequest Request containing the new password and confirm password
     * @return  a ResponseEntity containing the response of NewPasswordResponse
     * @throws Exception
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ResponseEntity<?> resetPasswordWithNewPassword(
            @RequestParam("token") String token,
            @RequestBody NewPasswordRequest newPasswordRequest) throws Exception {

        final NewPasswordResponse passwordResponse = userService.updatePassword(token, newPasswordRequest);
        return passwordResponse.isSuccess() ? new ResponseEntity(passwordResponse.getResponse(), HttpStatus.OK) : new ResponseEntity(passwordResponse.getResponse(), HttpStatus.BAD_REQUEST);
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
