package com.marinamooringmanagement.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Response class for responding to the Link send through email
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailResponse implements Serializable {

    private static final long serialVersionUID = 5526863507989L;

    /**
     * Flag to show whether the operation was a success or not.
     */
    private boolean isSuccess;

    /**
     * Response in String form
     */
    private String response;
}
