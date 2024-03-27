package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String fromMailID;

    @Override
    public SimpleMailMessage constructPasswordResetEmail(String contextPath, String token, String email) {
        String url = contextPath + "/api/v1/auth/resetPassword?token=" + token;
        String message = "Please visit this following link to reset your password \r\n" + url;
        return constructEmail("Reset Password", message, email);
    }

    private SimpleMailMessage constructEmail(String subject, String body, String toMailID) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        simpleMailMessage.setTo(toMailID);
        simpleMailMessage.setFrom(fromMailID);

        return simpleMailMessage;
    }
}
