package com.marinamooringmanagement.exception.model;

/**
 * Class to define Error Information such as detail and message for RESTful services.
 */
public class RestErrorInfo {
    /**
     * The detailed information about the error.
     */
    public final String detail;

    /**
     * The error message.
     */
    public final String message;

    /**
     * Constructs a RestErrorInfo object with the provided Exception and detail.
     *
     * @param ex     The Exception that occurred, used to derive the error message.
     * @param detail The detailed information about the error.
     */
    public RestErrorInfo(Exception ex, String detail) {
        this.message = ex.getLocalizedMessage();
        this.detail = detail;
    }
}

