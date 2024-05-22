package com.marinamooringmanagement.exception.handler;

import com.marinamooringmanagement.exception.*;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.Timestamp;

/**
 * Global exception handler to handle all exceptions in the application.
 * <p>
 * This class uses Spring's {@link ControllerAdvice} to handle exceptions thrown by any controller.
 * It provides methods to handle specific types of exceptions and return custom error responses.
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler implements ApplicationEventPublisherAware {

    protected ApplicationEventPublisher eventPublisher;

    /**
     * Sets the {@link ApplicationEventPublisher} that this object runs in.
     *
     * @param applicationEventPublisher the event publisher to be used by this object.
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    /**
     * Handle {@link MethodArgumentNotValidException}.
     * <p>
     * This method handles exceptions thrown when method argument validation fails.
     * It constructs a {@link BasicRestResponse} containing the error details.
     * </p>
     *
     * @param ex the exception thrown when method argument validation fails.
     * @return a {@link BasicRestResponse} containing the error details.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BasicRestResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        StringBuilder errorMessage = new StringBuilder();
        BindingResult result = ex.getBindingResult();
        for (FieldError fieldError : result.getFieldErrors()) {
            errorMessage.append(fieldError.getDefaultMessage()).append(" ");
        }

        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(errorMessage.substring(0, errorMessage.length() - 1));

        return response;
    }

    /**
     * Handle {@link RuntimeException}.
     * <p>
     * This method handles runtime exceptions and constructs a {@link BasicRestResponse} containing the error details.
     * </p>
     *
     * @param ex the runtime exception thrown.
     * @return a {@link BasicRestResponse} containing the error details.
     */
    @ExceptionHandler(RuntimeException.class)
    public BasicRestResponse handleRuntimeException(RuntimeException ex) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getLocalizedMessage());

        return response;
    }

    /**
     * Handle {@link DataFormatException}.
     * <p>
     * This method handles data format exceptions and constructs a {@link BasicRestResponse} containing the error details.
     * </p>
     *
     * @param ex the data format exception thrown.
     * @return a {@link BasicRestResponse} containing the error details.
     */
    @ExceptionHandler(DataFormatException.class)
    public BasicRestResponse handleDataFormatException(DataFormatException ex) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getLocalizedMessage());

        return response;
    }

    /**
     * Handle {@link DBOperationException}.
     * <p>
     * This method handles database operation exceptions and constructs a {@link BasicRestResponse} containing the error details.
     * </p>
     *
     * @param ex the database operation exception thrown.
     * @return a {@link BasicRestResponse} containing the error details.
     */
    @ExceptionHandler(DBOperationException.class)
    public BasicRestResponse handleDBOperationException(DBOperationException ex) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getLocalizedMessage());

        return response;
    }

    /**
     * Handle {@link InvalidObjectDetailsException}.
     * <p>
     * This method handles invalid object details exceptions and constructs a {@link BasicRestResponse} containing the error details.
     * </p>
     *
     * @param ex the invalid object details exception thrown.
     * @return a {@link BasicRestResponse} containing the error details.
     */
    @ExceptionHandler(InvalidObjectDetailsException.class)
    public BasicRestResponse handleInvalidObjectDetailsException(InvalidObjectDetailsException ex) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getLocalizedMessage());

        return response;
    }

    /**
     * Handle {@link InvalidPaginationRequestException}.
     * <p>
     * This method handles invalid pagination request exceptions and constructs a {@link BasicRestResponse} containing the error details.
     * </p>
     *
     * @param ex the invalid pagination request exception thrown.
     * @return a {@link BasicRestResponse} containing the error details.
     */
    @ExceptionHandler(InvalidPaginationRequestException.class)
    public BasicRestResponse handleInvalidPaginationRequestException(InvalidPaginationRequestException ex) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getLocalizedMessage());

        return response;
    }

    /**
     * Handle {@link ResourceNotFoundException}.
     * <p>
     * This method handles resource not found exceptions and constructs a {@link BasicRestResponse} containing the error details.
     * </p>
     *
     * @param ex the resource not found exception thrown.
     * @return a {@link BasicRestResponse} containing the error details.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public BasicRestResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getLocalizedMessage());

        return response;
    }
}