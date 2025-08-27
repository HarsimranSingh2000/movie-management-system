package com.entertainment.movie_management_system.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private final ErrorMapper errorMapper;

    public GlobalExceptionHandler(ErrorMapper errorMapper) {
        this.errorMapper = errorMapper;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
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

    return new ResponseEntity<>(errorMapper.createErrorMap(strBuilder.substring(0, strBuilder.length()-1)), HttpStatus.BAD_REQUEST);
}

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("{\"error\": \"" + ex.getMessage().replace("\"", "\\\"") + "\"}");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("{\"error\": \"" + ex.getMessage().replace("\"", "\\\"") + "\"}");
    }

    // You can add more handlers for other exception types as needed
}