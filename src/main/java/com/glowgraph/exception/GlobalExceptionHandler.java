package com.glowgraph.exception;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(
            IllegalArgumentException ex
    ) {
        return Map.of(
                "error",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(
            ConstraintViolationException ex
    ) {
        return Map.of(
                "error",
                ex.getConstraintViolations()
                        .iterator()
                        .next()
                        .getMessage()
        );
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodValidationException(
            HandlerMethodValidationException ex
    ) {
        return Map.of(
                "error",
                "Concern is required"
        );
    }
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingParameter(
            MissingServletRequestParameterException ex
    ) {
        return Map.of(
                "error",
                ex.getParameterName() + " is required"
        );
    }
}