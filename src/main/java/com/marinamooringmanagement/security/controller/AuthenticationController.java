package com.marinamooringmanagement.security.controller;

import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.request.NewPasswordRequest;
import com.marinamooringmanagement.response.EmailLinkResponse;
import com.marinamooringmanagement.response.NewPasswordResponse;
import com.marinamooringmanagement.security.config.JwtUtil;
import com.marinamooringmanagement.security.model.AuthenticationRequest;
import com.marinamooringmanagement.security.model.AuthenticationResponse;
import com.marinamooringmanagement.request.ForgetPasswordEmailRequest;
import com.marinamooringmanagement.service.EmailService;
import com.marinamooringmanagement.service.PasswordResetService;
import com.marinamooringmanagement.service.UserService;
import com.marinamooringmanagement.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;


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
    private PasswordResetService passwordResetService;

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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        return generateAuthenticationResponse(authenticationRequest.getUsername());
    }

    @RequestMapping(value = "/forgetPassword", method = RequestMethod.POST)
    public ResponseEntity<?> forgetPassword(
            HttpServletRequest request,
            @Valid @RequestBody ForgetPasswordEmailRequest forgetPasswordEmailRequest) throws Exception {
        try {
            String resetPasswordToken = passwordResetService.createPasswordResetToken(forgetPasswordEmailRequest.getEmail());
            String contextPath = request.getScheme() + "://" + request.getServerName() + ":"  + request.getServerPort();
            javaMailSender.send(emailService.constructPasswordResetEmail(contextPath, resetPasswordToken, forgetPasswordEmailRequest.getEmail()));
            return ResponseEntity.ok("Mail Send Successfully!!!");
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
    public ResponseEntity<?> resetPassword(
            @RequestParam("token") String token) throws Exception {
        EmailLinkResponse response = userService.checkEmailAndTokenValid(token);
        return response.isSuccess() ? new ResponseEntity(response.getResponse(), HttpStatus.OK) : new ResponseEntity(response.getResponse(), HttpStatus.BAD_REQUEST);
    }

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
