package com.marinamooringmanagement.exception;

/**
 * Exception thrown to indicate that an object's details are invalid.
 */
public final class InvalidObjectDetailsException extends RuntimeException {

    /**
     * Constructs a new InvalidObjectDetailsException with no detail message.
     */
    public InvalidObjectDetailsException() {
        super();
    }

    /**
     * Constructs a new InvalidObjectDetailsException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method).
     */
    public InvalidObjectDetailsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidObjectDetailsException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public InvalidObjectDetailsException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidObjectDetailsException with the specified cause and a detail message of
     * (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public InvalidObjectDetailsException(Throwable cause) {
        super(cause);
    }
}
