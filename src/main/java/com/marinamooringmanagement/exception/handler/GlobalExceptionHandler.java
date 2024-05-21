package com.marinamooringmanagement.exception.handler;

import com.marinamooringmanagement.exception.*;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.Timestamp;

/**
 * Global exception handler to handle all exceptions in the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle MethodArgumentNotValidException.
     *
     * @param ex the exception thrown when method argument validation fails.
     * @return a ResponseEntity containing the error details.
     */
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
        response.setMessage(errorMessage.substring(0, errorMessage.length()-1));

        return response;
    }

    @ExceptionHandler(RuntimeException.class)
    public BasicRestResponse handleRuntimeException(RuntimeException ex) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getLocalizedMessage());

        return response;
    }

    @ExceptionHandler(DataFormatException.class)
    public BasicRestResponse handleDataFormatException(DataFormatException ex) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getLocalizedMessage());

        return response;
    }

    @ExceptionHandler(DBOperationException.class)
    public BasicRestResponse handleDBOperationException(DBOperationException ex) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getLocalizedMessage());

        return response;
    }

    @ExceptionHandler(InvalidObjectDetailsException.class)
    public BasicRestResponse handleInvalidObjectDetailsException(InvalidObjectDetailsException ex) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getLocalizedMessage());

        return response;
    }

    @ExceptionHandler(InvalidPaginationRequestException.class)
    public BasicRestResponse handleInvalidPaginationRequestException(InvalidPaginationRequestException ex) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getLocalizedMessage());

        return response;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public BasicRestResponse handleResourceNotFoundException(ResourceNotFoundException ex) {
        BasicRestResponse response = BasicRestResponse.builder().build();

        response.setTime(new Timestamp(System.currentTimeMillis()));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getLocalizedMessage());

        return response;
    }

}
