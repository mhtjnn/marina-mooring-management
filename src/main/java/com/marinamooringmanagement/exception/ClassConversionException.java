package com.marinamooringmanagement.exception;

public class ClassConversionException extends RuntimeException{

    /**
     * Constructs a new ClassConversionException with no detail message.
     */
    public ClassConversionException() {
        super();
    }

    /**
     * Constructs a new ClassConversionException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method).
     */
    public ClassConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ClassConversionException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public ClassConversionException(String message) {
        super(message);
    }

    /**
     * Constructs a new ClassConversionException with the specified cause and a detail message of
     * (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public ClassConversionException(Throwable cause) {
        super(cause);
    }

}
