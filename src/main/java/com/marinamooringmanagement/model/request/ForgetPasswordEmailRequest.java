package com.marinamooringmanagement.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class for accepting an email address for sending a password reset link.
 * This class is used to accept the email to which the password reset link will be sent.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordEmailRequest {
    /**
     * The email address to which the password reset link will be sent.
     */
    @NotEmpty(message = "Email can't be blank")
    private String email;
}
