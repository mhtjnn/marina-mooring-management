package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.entity.User;
import org.springframework.mail.SimpleMailMessage;

import java.util.Locale;

public interface EmailService {
    SimpleMailMessage constructPasswordResetEmail(String contextPath, String token, String email);
}
