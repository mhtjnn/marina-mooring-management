package com.marinamooringmanagement.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents a template for a reset password email.
 * It includes fields for the recipient's email ID, subject, and body.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordEmailTemplate {

    /**
     * The email ID of the recipient. Cannot be null.
     */
    @NotNull(message = "To Mail ID cannot be blank")
    private String toMailId;

    /**
     * The subject of the email.
     */
    private String subject;

    /**
     * The body content of the email.
     */
    private String body;

}

