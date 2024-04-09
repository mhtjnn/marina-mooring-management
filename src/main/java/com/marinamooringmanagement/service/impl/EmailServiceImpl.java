package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.model.request.ForgetPasswordEmailRequest;
import com.marinamooringmanagement.model.request.ResetPasswordEmailTemplate;
import com.marinamooringmanagement.model.request.SendEmailRequest;
import com.marinamooringmanagement.model.response.SendEmailResponse;
import com.marinamooringmanagement.service.EmailService;
import com.marinamooringmanagement.service.TokenService;
import com.marinamooringmanagement.utils.EmailUtils;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Service implementation class for Email related methods.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TokenService tokenService;

    @Value("${spring.mail.username}")
    private String fromMailID;

    /**
     * Sends a forget password email using the provided request and email template.
     * Generates a reset password token and constructs the email message with the reset URL.
     *
     * @param request                   The HttpServletRequest object to get server information.
     * @param forgetPasswordEmailRequest The ForgetPasswordEmailRequest containing email and other details.
     * @return SendEmailResponse        The response indicating if the email was sent successfully or not.
     */
    @Override
    public SendEmailResponse sendForgetPasswordEMail(HttpServletRequest request, ForgetPasswordEmailRequest forgetPasswordEmailRequest) {
        SendEmailResponse response = SendEmailResponse.builder().build();
        try {

            ResetPasswordEmailTemplate template = ResetPasswordEmailTemplate.builder().build();

            String resetPasswordToken = tokenService.createPasswordResetToken(forgetPasswordEmailRequest.getEmail());
            String contextPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

            String url = contextPath + "/api/v1/auth/resetPassword?token=" + resetPasswordToken;
            String message = "Please visit this following link to reset your password: " + url;

            template.setToMailId(forgetPasswordEmailRequest.getEmail());
            template.setSubject("Reset Password");
            template.setBody(message);

            SendEmailRequest sendEmailRequest = emailUtils.generateEmailRequest(template);

            return sendEmail(sendEmailRequest);
        } catch (Exception e) {
            response.setResponse("Error occurred while sending email");
            response.setSuccess(false);
            return response;
        }
    }

    /**
     * Sends an email using the provided SendEmailRequest object.
     * Uses JavaMailSender to create and send the MimeMessage.
     *
     * @param sendEmailRequest The SendEmailRequest containing email details.
     * @return SendEmailResponse The response indicating if the email was sent successfully or not.
     */
    @Override
    public SendEmailResponse sendEmail(SendEmailRequest sendEmailRequest) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false);
            mimeMessageHelper.setFrom(fromMailID);

            if(CollectionUtils.isNotEmpty(sendEmailRequest.getToList())) {
                mimeMessageHelper.setTo(sendEmailRequest.getToList().toArray(new String[sendEmailRequest.getToList().size()]));
            }

            if(CollectionUtils.isNotEmpty(sendEmailRequest.getBccList())) {
                mimeMessageHelper.setTo(sendEmailRequest.getBccList().toArray(new String[sendEmailRequest.getBccList().size()]));
            }

            if(CollectionUtils.isNotEmpty(sendEmailRequest.getCcList())) {
                mimeMessageHelper.setTo(sendEmailRequest.getCcList().toArray(new String[sendEmailRequest.getCcList().size()]));
            }

            if(StringUtils.isNotEmpty(sendEmailRequest.getSubject())) {
                mimeMessageHelper.setSubject(sendEmailRequest.getSubject());
            }

            if(StringUtils.isNotEmpty(sendEmailRequest.getBody())) {
                mimeMessageHelper.setText(sendEmailRequest.getBody(), true);
            }

            javaMailSender.send(mimeMessage);
            return new SendEmailResponse(true, "Email send Successfully!!!");

        } catch (Exception e) {
            log.info("Error occurred while sending email: {}", e.getLocalizedMessage());
            return new SendEmailResponse(false, "Error occurred while sending email");
        }
    }

}
