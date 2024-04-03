package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.request.ForgetPasswordEmailRequest;
import com.marinamooringmanagement.model.response.EmailLinkResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.SimpleMailMessage;


/**
 * Service interface for handling email operations.
 */
public interface EmailService {

    /**
     * Constructs a password reset email message.
     *
     * @param contextPath the context path of the application
     * @param token       the reset token
     * @param email       the recipient's email address
     * @return a SimpleMailMessage representing the password reset email
     */
    SimpleMailMessage constructPasswordResetEmail(String contextPath, String token, String email);

    /**
     * Sends an email with a password reset link.
     *
     * @param request                 the HTTP servlet request
     * @param forgetPasswordEmailRequest the request containing the email details
     * @return an EmailLinkResponse indicating the status of the email sending process
     */
    EmailLinkResponse sendMail(HttpServletRequest request, ForgetPasswordEmailRequest forgetPasswordEmailRequest);
}

