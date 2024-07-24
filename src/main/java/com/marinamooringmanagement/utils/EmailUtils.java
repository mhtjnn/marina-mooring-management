package com.marinamooringmanagement.utils;

import com.marinamooringmanagement.model.request.ResetPasswordEmailTemplate;
import com.marinamooringmanagement.model.request.SendEmailRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for generating SendEmailRequest objects.
 */
public class EmailUtils {

    /**
     * Generates a SendEmailRequest object based on the provided ResetPasswordEmailTemplate.
     *
     * @param template The ResetPasswordEmailTemplate containing email details.
     * @return SendEmailRequest The generated SendEmailRequest object.
     */
    public static SendEmailRequest generateEmailRequest(final ResetPasswordEmailTemplate template) {
        final SendEmailRequest sendEmailRequest = SendEmailRequest.builder().build();

        // Extract and set the 'to' email address
        final List<String> toMailList = new ArrayList<>();
        toMailList.add(template.getToMailId());
        if (CollectionUtils.isNotEmpty(toMailList)) {
            sendEmailRequest.setToList(toMailList);
        }

        // Set the subject if not empty
        if (StringUtils.isNotEmpty(template.getSubject())) {
            sendEmailRequest.setSubject(template.getSubject());
        }

        // Set the body if not empty
        if (StringUtils.isNotEmpty(template.getBody())) {
            sendEmailRequest.setBody(template.getBody());
        }

        return sendEmailRequest;
    }
}