package com.marinamooringmanagement.exception;


public class QBOOperationException extends RuntimeException{

    /**
     * Constructs a new QBOOperationException with no detail message.
     */
    public QBOOperationException() {
        super();
    }

    /**
     * Constructs a new QBOOperationException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method).
     */
    public QBOOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new QBOOperationException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public QBOOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new QBOOperationException with the specified cause and a detail message of
     * (cause==null ? null : cause.toString()) (which typically contains the class and detail message of cause).
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public QBOOperationException(Throwable cause) {
        super(cause);
    }

}
