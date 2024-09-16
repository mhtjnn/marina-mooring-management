package com.marinamooringmanagement.exception;

public class MathException extends RuntimeException{

    /**
     * Constructs a new MathException with no detail message.
     */
    public MathException() {
        super();
    }

    /**
     * Constructs a new MathException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method).
     */
    public MathException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new MathException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public MathException(String message) {
        super(message);
    }

    /**
     * Constructs a new MathException with the specified cause and a detail message of
     * (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public MathException(Throwable cause) {
        super(cause);
    }
}
