package com.entertainment.movie_management_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.swagger.v3.oas.annotations.Hidden;

import org.springframework.web.bind.MethodArgumentNotValidException;
@Hidden
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private final ErrorMapper errorMapper;

    public GlobalExceptionHandler(ErrorMapper errorMapper) {
        this.errorMapper = errorMapper;
    }

    // Helper method to build error message from validation errors
    private String buildValidationErrorMessage(MethodArgumentNotValidException e) {
        StringBuilder strBuilder = new StringBuilder();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName;
            try {
                fieldName = ((FieldError) error).getField();
            } catch (ClassCastException ex) {
                fieldName = error.getObjectName();
            }
            String message = error.getDefaultMessage();
            strBuilder.append(String.format("%s : %s ; ", fieldName, message));
        });

        // Remove the last " ; " if present
        if (strBuilder.length() > 2) {
            strBuilder.setLength(strBuilder.length() - 2);
        }
        return strBuilder.toString();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Hidden
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMsg = buildValidationErrorMessage(e);
        return new ResponseEntity<>(errorMapper.createErrorMap(errorMsg), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @Hidden
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(errorMapper.createErrorMap(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    @Hidden
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(errorMapper.createErrorMap(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
    
    // You can add more handlers for other exception types as needed
}