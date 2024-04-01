package com.marinamooringmanagement.exception;

/**
 * This exception is thrown to indicate an error related to data format.
 */
public final class DataFormatException extends RuntimeException {

    /**
     * Constructs a new DataFormatException with no detail message.
     */
    public DataFormatException() {
        super();
    }

    /**
     * Constructs a new DataFormatException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method).
     */
    public DataFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DataFormatException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public DataFormatException(String message) {
        super(message);
    }

    /**
     * Constructs a new DataFormatException with the specified cause and a detail message of
     * (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public DataFormatException(Throwable cause) {
        super(cause);
    }
}

