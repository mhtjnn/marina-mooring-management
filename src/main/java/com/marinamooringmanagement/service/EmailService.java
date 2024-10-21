package com.marinamooringmanagement.service;

import com.marinamooringmanagement.model.entity.WorkOrder;
import com.marinamooringmanagement.model.request.ForgetPasswordEmailRequest;
import com.marinamooringmanagement.model.request.SendEmailRequest;
import com.marinamooringmanagement.model.response.SendEmailResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * EmailService interface defines methods for sending different types of emails.
 */
public interface EmailService {

    /**
     * Sends a forget password email using the provided request and email details.
     *
     * @param request                   The HttpServletRequest object to get server information.
     * @param forgetPasswordEmailRequest The ForgetPasswordEmailRequest containing email and other details.
     * @return SendEmailResponse        The response indicating if the email was sent successfully or not.
     */
    SendEmailResponse sendForgetPasswordEmail(final HttpServletRequest request, final ForgetPasswordEmailRequest forgetPasswordEmailRequest);

    SendEmailResponse sendNotificationForWorkOrder(final WorkOrder workOrder);

    /**
     * Sends an email using the provided SendEmailRequest object.
     *
     * @param sendEmailRequest The SendEmailRequest containing email details.
     * @return SendEmailResponse The response indicating if the email was sent successfully or not.
     */
    SendEmailResponse sendEmail(final SendEmailRequest sendEmailRequest);
}