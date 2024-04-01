package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.request.ForgetPasswordEmailRequest;
import com.marinamooringmanagement.model.response.EmailLinkResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mail.SimpleMailMessage;

import java.util.Locale;

public interface EmailService {
    SimpleMailMessage constructPasswordResetEmail(String contextPath, String token, String email);

    EmailLinkResponse sendMail(HttpServletRequest request, ForgetPasswordEmailRequest forgetPasswordEmailRequest);
}
