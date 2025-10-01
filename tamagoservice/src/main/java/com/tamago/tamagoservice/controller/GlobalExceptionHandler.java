package com.tamago.tamagoservice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.tamago.tamagoservice.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
        HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse resp = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation Failed",
                "Request validation failed", errors);
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse resp = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), null);
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(com.tamago.tamagoservice.exception.DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(com.tamago.tamagoservice.exception.DuplicateResourceException ex) {
        ErrorResponse resp = new ErrorResponse(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(), null);
        resp.setCode("DUPLICATE_RESOURCE");
        resp.setHint("Choose a different pseudo or check that the user does not already exist.");
        return new ResponseEntity<>(resp, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(com.tamago.tamagoservice.exception.AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthFail(com.tamago.tamagoservice.exception.AuthenticationFailedException ex) {
        ErrorResponse resp = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", ex.getMessage(), null);
        resp.setCode("AUTH_FAILED");
        resp.setHint("Check pseudo and password; passwords are case-sensitive.");
        return new ResponseEntity<>(resp, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
    ErrorResponse resp = new ErrorResponse(HttpStatus.CONFLICT.value(), "Data Integrity Violation",
        ex.getMostSpecificCause().getMessage(), null);
    resp.setCode("DB_CONSTRAINT_VIOLATION");
    resp.setHint("This usually means a unique constraint was violated (duplicate pseudo). Retry with a different value.");
    return new ResponseEntity<>(resp, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(com.tamago.tamagoservice.exception.ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(com.tamago.tamagoservice.exception.ResourceNotFoundException ex) {
        ErrorResponse resp = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), null);
        resp.setCode("RESOURCE_NOT_FOUND");
        resp.setHint("Verify the id or ensure the resource exists before requesting.");
        return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        ErrorResponse resp = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                ex.getMessage(), null);
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
