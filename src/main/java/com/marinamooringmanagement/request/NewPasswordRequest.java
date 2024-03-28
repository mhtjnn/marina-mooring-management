package com.marinamooringmanagement.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request class for setting a new password.
 * This class is used to accept a new password and its confirmation.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewPasswordRequest {
    /**
     * The new password to be set.
     */
    @NotNull(message = "New Password cannot be blank")
    private String newPassword;

    /**
     * The confirmation of the new password.
     */
    @NotNull(message = "Confirm Password cannot be blank")
    private String confirmPassword;
}
