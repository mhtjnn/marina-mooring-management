package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.model.request.ForgetPasswordEmailRequest;
import com.marinamooringmanagement.model.response.EmailLinkResponse;
import com.marinamooringmanagement.service.EmailService;
import com.marinamooringmanagement.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


/**
 * Service implementation class for Email related methods.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TokenService tokenService;

    @Value("${spring.mail.username}")
    private String fromMailID;

    /**
     * Function to create {@link SimpleMailMessage} for Reset Password functionality.
     * @param contextPath
     * @param token
     * @param email
     * @return
     */
    @Override
    public SimpleMailMessage constructPasswordResetEmail(String contextPath, String token, String email) {
        String url = contextPath + "/api/v1/auth/resetPassword?token=" + token;
        String message = "Please visit this following link to reset your password \r\n" + url;
        return constructEmail("Reset Password", message, email);
    }

    /**
     * Sends an email with a password reset link.
     *
     * @param request                    the HTTP servlet request
     * @param forgetPasswordEmailRequest the request containing the email details
     * @return an EmailLinkResponse indicating the status of the email sending process
     */
    @Override
    public EmailLinkResponse sendMail(HttpServletRequest request, ForgetPasswordEmailRequest forgetPasswordEmailRequest) {
        EmailLinkResponse response = EmailLinkResponse.builder().build();
        try {
            // Create a password reset token for the provided email address
            String resetPasswordToken = tokenService.createPasswordResetToken(forgetPasswordEmailRequest.getEmail());

            // Construct the context path for the email link
            String contextPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

            // Send the password reset email
            javaMailSender.send(constructPasswordResetEmail(contextPath, resetPasswordToken, forgetPasswordEmailRequest.getEmail()));

            // Set the response details for success
            response.setResponse("Email Sent Successfully");
            response.setSuccess(true);
        } catch (Exception e) {
            log.info("Error occurred while sending email", e);
            // Set the response details for failure
            response.setResponse("Error occurred while sending email");
            response.setSuccess(false);
        }
        return response;
    }


    /**
     * Helper function to construct email using subject, body and toMailID.
     * @param subject
     * @param body
     * @param toMailID
     * @return
     */
    private SimpleMailMessage constructEmail(String subject, String body, String toMailID) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(toMailID);
        simpleMailMessage.setFrom(fromMailID);

        return simpleMailMessage;
    }
}
