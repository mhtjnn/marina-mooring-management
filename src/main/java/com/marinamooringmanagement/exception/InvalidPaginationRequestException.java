package com.marinamooringmanagement.exception;

/**
 * Exception thrown to indicate an invalid pagination request.
 * This typically occurs when there is an issue with the pagination request parameters.
 */
public final class InvalidPaginationRequestException extends RuntimeException {

    /**
     * Constructs a new InvalidPaginationRequestException with no detail message.
     */
    public InvalidPaginationRequestException() {
        super();
    }

    /**
     * Constructs a new InvalidPaginationRequestException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method).
     */
    public InvalidPaginationRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidPaginationRequestException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public InvalidPaginationRequestException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidPaginationRequestException with the specified cause and a detail message of
     * (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public InvalidPaginationRequestException(Throwable cause) {
        super(cause);
    }
}
