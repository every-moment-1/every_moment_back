package com.rookies4.every_moment.common;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        FieldError fe = ex.getBindingResult().getFieldError();
        String field = fe != null ? fe.getField() : null;
        String msg = fe != null ? fe.getDefaultMessage() : ErrorCode.VALIDATION_ERROR.message;
        var body = ApiErrorResponse.of(ErrorCode.VALIDATION_ERROR, field, msg);
        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.status).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraint(ConstraintViolationException ex) {
        var body = ApiErrorResponse.of(ErrorCode.VALIDATION_ERROR, null, ex.getMessage());
        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.status).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCred(BadCredentialsException ex) {
        var body = ApiErrorResponse.of(ErrorCode.INVALID_CREDENTIALS, null, null);
        return ResponseEntity.status(ErrorCode.INVALID_CREDENTIALS.status).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleDenied(AccessDeniedException ex) {
        var body = ApiErrorResponse.of(ErrorCode.ACCESS_DENIED, null, null);
        return ResponseEntity.status(ErrorCode.ACCESS_DENIED.status).body(body);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiErrorResponse> handleExpired(ExpiredJwtException ex) {
        var body = ApiErrorResponse.of(ErrorCode.TOKEN_EXPIRED, null, null);
        return ResponseEntity.status(ErrorCode.TOKEN_EXPIRED.status).body(body);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiErrorResponse> handleJwt(JwtException ex) {
        var body = ApiErrorResponse.of(ErrorCode.TOKEN_INVALID, null, ex.getMessage());
        return ResponseEntity.status(ErrorCode.TOKEN_INVALID.status).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(DataIntegrityViolationException ex) {
        // best effort: decide field by message
        String message = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        ErrorCode ec = message != null && message.toLowerCase().contains("email") ? ErrorCode.DUPLICATE_EMAIL : ErrorCode.DUPLICATE_USERNAME;
        var body = ApiErrorResponse.of(ec, ec == ErrorCode.DUPLICATE_EMAIL ? "email" : "username", null);
        return ResponseEntity.status(ec.status).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(RuntimeException ex) {
        var body = ApiErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, null, ex.getMessage());
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.status).body(body);
    }
}