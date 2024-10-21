package com.marinamooringmanagement.exception;

public class ResourceNotProvidedException extends RuntimeException {

    /**
     * Constructs a new ResourceNotProvidedException with no detail message.
     */
    public ResourceNotProvidedException() {
        super();
    }

    /**
     * Constructs a new ResourceNotProvidedException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method).
     */
    public ResourceNotProvidedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ResourceNotProvidedException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public ResourceNotProvidedException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotProvidedException with the specified cause and a detail message of
     * (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public ResourceNotProvidedException(Throwable cause) {
        super(cause);
    }

}
