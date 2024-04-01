package com.marinamooringmanagement.security.exception;

import javax.naming.AuthenticationException;

/**
 * Exception class for Email NOT FOUND Exception.
 * This exception is thrown when an email is not found during authentication or authorization processes.
 */
public class EmailNotFoundException extends AuthenticationException {
    /**
     * Constructs a new EmailNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause   the cause (which is saved for later retrieval by the getCause() method)
     */
    public EmailNotFoundException(String message, Throwable cause) {
        super(message);
    }

    /**
     * Constructs a new EmailNotFoundException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public EmailNotFoundException(String message) {
        super(message);
    }
}
