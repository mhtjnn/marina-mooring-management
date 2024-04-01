package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;

/**
 * Response class handling Api for setting new password for the user
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewPasswordResponse {
    /**
     * Flag to show whether the operation was a success or not.
     */
    private boolean isSuccess;

    /**
     * Response in String form
     */
    private String response;
}
