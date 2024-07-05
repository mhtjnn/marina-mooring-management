package com.marinamooringmanagement.security.controller;

import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;
import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.Token;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.TokenRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.model.request.NewPasswordRequest;
import com.marinamooringmanagement.model.response.SendEmailResponse;
import com.marinamooringmanagement.security.config.LogoutService;
import com.marinamooringmanagement.security.util.JwtUtil;
import com.marinamooringmanagement.security.model.AuthenticationRequest;
import com.marinamooringmanagement.security.model.AuthenticationResponse;
import com.marinamooringmanagement.model.request.ForgetPasswordEmailRequest;
import com.marinamooringmanagement.service.EmailService;
import com.marinamooringmanagement.service.UserService;
import com.marinamooringmanagement.service.TokenService;
import io.jsonwebtoken.io.Decoders;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Optional;

import java.util.Date;
import java.util.List;


/**
 * Controller class for handling authentication-related endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@CrossOrigin
public class AuthenticationController extends GlobalExceptionHandler {

    private static final String normalTokenStr = "NORMAL_TOKEN";
    private static final String refreshTokenStr = "REFRESH_TOKEN";

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

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LogoutService logoutService;

    /**
     * Endpoint to create an authentication token based on the provided credentials.
     *
     * @param authenticationRequest the authentication request containing username and password
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
            @Parameter(description = "Username and Password", schema = @Schema(implementation = AuthenticationRequest.class)) final @Valid @RequestBody AuthenticationRequest authenticationRequest
    ) throws Exception {
        final AuthenticationResponse authenticationResponse = AuthenticationResponse.builder().build();

        try {
            Optional<User> optionalUser = userRepository.findByEmail(authenticationRequest.getUsername());

            if(optionalUser.isEmpty()) throw new ResourceNotFoundException(String.format("Sorry, we can't find an account with %1$s", authenticationRequest.getUsername()));
            
            byte[] keyBytes = Decoders.BASE64.decode(authenticationRequest.getPassword());

            authenticationRequest.setPassword(new String(keyBytes, StandardCharsets.UTF_8));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );
            return generateAuthenticationResponse(authenticationRequest.getUsername(), authenticationResponse);
        } catch (Exception e) {
            final BasicRestResponse response = BasicRestResponse.builder().build();

            if(e.getLocalizedMessage().equals("Bad credentials"))
                response.setMessage(String.format("Incorrect password for %1$s", authenticationRequest.getUsername()));
            else
                response.setMessage(e.getLocalizedMessage());
            response.setTime(new Timestamp(System.currentTimeMillis()));
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


    @Operation(
            tags = "User Logout",
            description = "API to logout the user",
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
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public BasicRestResponse sessionLogout(
            final HttpServletRequest request
    ) throws Exception {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            logoutService.logout(request);
            response.setMessage("User logout successfully");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    /**
     * Endpoint to refresh the user token.
     *
     * @param refreshToken the refresh token value
     * @return a ResponseEntity containing the refreshed authentication response
     * @throws Exception if an error occurs during token refresh
     */
    @Operation(
            tags = "User Refresh",
            description = "API to refresh the user",
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
    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationTokenFromRefreshToken(
            @Parameter(description = "Refresh Token", schema = @Schema(implementation = String.class)) final @RequestParam("refreshToken") String refreshToken
    ) throws Exception {
        final AuthenticationResponse authenticationResponse = AuthenticationResponse.builder().build();
        try {
            return generateNormalTokenFromRefreshToken(refreshToken, authenticationResponse);
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
     * Generates a new authentication token from a refresh token.
     *
     * @param refreshToken the refresh token
     * @param response the authentication response to populate with the new token
     * @return a ResponseEntity containing the updated authentication response
     */
    private ResponseEntity<?> generateNormalTokenFromRefreshToken(String refreshToken, final AuthenticationResponse response) {
        try {
            final String username = jwtUtil.getUsernameFromToken(refreshToken);
            final UserDto emp = userService.findByEmailAddress(username);
            final String token = jwtUtil.generateToken(emp, normalTokenStr);

            Date refreshTokenExpirationTime = jwtUtil.getExpireTimeFromToken(refreshToken);

            final List<Token> refreshTokenEntityList = tokenRepository.findTokenEntityByRefreshToken(refreshToken);

            if(refreshTokenEntityList.isEmpty()) throw new ResourceNotFoundException(String.format("No tokens found with the given token: %1$s", refreshToken));

            String newRefreshToken = refreshToken;

            if (refreshTokenExpirationTime.before(new Date())) newRefreshToken = jwtUtil.generateToken(emp, refreshTokenStr);

            tokenService.saveToken(emp, token, refreshToken);
            response.setToken(token);
            response.setRefreshToken(newRefreshToken);
            response.setUser(emp);
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Generates the authentication response containing the JWT token and user details.
     *
     * @param username the username for which to generate the authentication response
     * @return a ResponseEntity containing the authentication response
     */
    private ResponseEntity<?> generateAuthenticationResponse(final String username, final AuthenticationResponse response) {
        final UserDto emp = userService.findByEmailAddress(username);
        final String token = jwtUtil.generateToken(emp, normalTokenStr);
        final String refreshToken = jwtUtil.generateToken(emp, refreshTokenStr);
        tokenService.saveToken(emp, token, refreshToken);
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setUser(emp);
        response.setStatus(HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
