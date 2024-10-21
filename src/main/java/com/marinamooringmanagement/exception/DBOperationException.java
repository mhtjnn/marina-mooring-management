package com.marinamooringmanagement.exception;

/**
 * This exception is thrown to indicate an error during database operations.
 */
public final class DBOperationException extends RuntimeException {

    /**
     * Constructs a new DBOperationException with no detail message.
     */
    public DBOperationException() {
        super();
    }

    /**
     * Constructs a new DBOperationException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method).
     */
    public DBOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DBOperationException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public DBOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new DBOperationException with the specified cause and a detail message of
     * (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public DBOperationException(Throwable cause) {
        super(cause);
    }
}

