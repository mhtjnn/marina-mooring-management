package com.marinamooringmanagement.security.controller;

import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.model.request.NewPasswordRequest;
import com.marinamooringmanagement.model.response.SendEmailResponse;
import com.marinamooringmanagement.security.config.JwtUtil;
import com.marinamooringmanagement.security.model.AuthenticationRequest;
import com.marinamooringmanagement.security.model.AuthenticationResponse;
import com.marinamooringmanagement.model.request.ForgetPasswordEmailRequest;
import com.marinamooringmanagement.service.EmailService;
import com.marinamooringmanagement.service.UserService;
import com.marinamooringmanagement.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

import java.sql.Timestamp;
import java.util.List;


/**
 * Controller class for handling authentication-related endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@CrossOrigin("*")
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
    @Operation(
            tags = "User Login",
            description = "API to login the user",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = AuthenticationResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Forbidden",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "403"
                    )
            }

    )
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(
            @Parameter(description = "Username and Password", schema = @Schema(implementation = AuthenticationRequest.class)) final @RequestBody AuthenticationRequest authenticationRequest,
            final HttpServletRequest request
    ) throws Exception {
        final AuthenticationResponse authenticationResponse = AuthenticationResponse.builder().build();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );
            return generateAuthenticationResponse(authenticationRequest.getUsername(), authenticationResponse);
        } catch (Exception e) {
            final BasicRestResponse response = BasicRestResponse.builder().build();
            response.setMessage("Authentication failed");
            response.setTime(new Timestamp(System.currentTimeMillis()));
            response.setErrorList(List.of(e.getMessage()));
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return new ResponseEntity(response, HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Function to send Link through email for password reset functionality.
     * @param request the HTTP servlet request
     * @param forgetPasswordEmailRequest Request containing the email where the reset password link has to be sent.
     * @return a ResponseEntity containing the response
     * @throws Exception
     */
    @Operation(
            tags = "Reset Password Email",
            description = "API to send a link to reset password through email",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = SendEmailResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = SendEmailResponse.class), mediaType = "application/json") },
                            responseCode = "400"
                    )
            }

    )
    @RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
    public ResponseEntity<?> forgetPassword(
            final HttpServletRequest request,
            @Parameter(description = "Registered email of the user", schema = @Schema(implementation = ForgetPasswordEmailRequest.class)) final @Valid @RequestBody ForgetPasswordEmailRequest forgetPasswordEmailRequest) throws Exception {
        final SendEmailResponse response = emailService.sendForgetPasswordEmail(request, forgetPasswordEmailRequest);
        return response.isSuccess() ? new ResponseEntity(response, HttpStatus.OK) : new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }


    /**
     * Function to reset password with the new password.
     * @param token The reset password JWT
     * @param newPasswordRequest Request containing the new password and confirm password
     * @return  a ResponseEntity containing the response of NewPasswordResponse
     * @throws Exception
     */
    @Operation(
            tags = "Reset password with the New password",
            description = "API to reset old password with the new password",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "500"
                    )
            }

    )
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public BasicRestResponse resetPasswordWithNewPassword(
            @Parameter(description = "Reset Password Token", schema = @Schema(implementation = String.class)) final @RequestParam("token") String token,
            @Parameter(description = "New Password", schema = @Schema(implementation = NewPasswordRequest.class)) final @RequestBody NewPasswordRequest newPasswordRequest) throws Exception {
        return userService.updatePassword(token, newPasswordRequest);
    }


    /**
     * Function to validate email(existence) and token.
     * @param token The Reset Password JWT
     * @return  a ResponseEntity containing the response from EmailLinkResponse
     * @throws Exception
     */
    @Operation(
            tags = "Validate email and reset password token",
            description = "API to validate email and reset password token",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            content = { @Content(schema = @Schema(implementation = BasicRestResponse.class), mediaType = "application/json") },
                            responseCode = "500"
                    )
            }

    )
    @RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
    public BasicRestResponse validateEmailAndToken(
            @RequestParam("token") final String token) throws Exception {
        return userService.checkEmailAndTokenValid(token);
    }


    /**
     * Generates the authentication response containing the JWT token and user details.
     *
     * @param username the username for which to generate the authentication response
     * @return a ResponseEntity containing the authentication response
     */
    private ResponseEntity<?> generateAuthenticationResponse(final String username, final AuthenticationResponse response) {
        final UserDto emp = userService.findByEmailAddress(username);
        final String token = jwtUtil.generateToken(emp);
        tokenService.saveToken(emp, token);
        response.setToken(token);
        response.setUser(emp);
        response.setStatus(HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}