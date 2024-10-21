package com.marinamooringmanagement.security.exception;

import com.marinamooringmanagement.exception.handler.GlobalExceptionHandler;

public class AuthorizationException extends RuntimeException {

    /**
     * Constructs a new AuthorizationException with no detail message.
     */
    public AuthorizationException() {
        super();
    }

    /**
     * Constructs a new AuthorizationException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method).
     */
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new AuthorizationException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public AuthorizationException(String message) {
        super(message);
    }

    /**
     * Constructs a new AuthorizationException with the specified cause and a detail message of
     * (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public AuthorizationException(Throwable cause) {
        super(cause);
    }


}
