package com.marinamooringmanagement.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a request to send an email.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailRequest {

    /**
     * List of email addresses to send the email to.
     */
    @NotNull(message = "To List cannot be empty")
    private List<String> toList;

    /**
     * List of email addresses to send a copy (CC) of the email to.
     */
    private List<String> ccList;

    /**
     * List of email addresses to send a blind copy (BCC) of the email to.
     */
    private List<String> bccList;

    /**
     * The subject of the email.
     */
    private String subject;

    /**
     * The body content of the email.
     */
    private String body;

    /**
     * The file path or URL of an attachment to include in the email.
     */
    private String attachment;
}
