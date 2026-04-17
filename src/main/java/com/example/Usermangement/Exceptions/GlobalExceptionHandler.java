package com.example.Usermangement.Exceptions;


import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiError> build(HttpStatus status, String message, String path){
        ApiError body = new ApiError(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            path    
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyRegistered(
        EmailAlreadyRegisteredException ex, HttpServletRequest req
    ){
        return build(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(PhoneNumberAlreadyRegisteredException.class)
    public ResponseEntity<ApiError> handlePhoneNumberAlreadyRegistered(
        PhoneNumberAlreadyRegisteredException ex, HttpServletRequest req
    ){
        return build(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(
        InvalidCredentialsException ex, HttpServletRequest req
    ){
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req.getRequestURI());
    }
    
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> handleInvalidRefreshToken(
        InvalidRefreshTokenException ex, HttpServletRequest req
    ){
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiError> handleAuthException(
        AuthException ex, HttpServletRequest req
    ){
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiError> handleInvalidOtp(
        InvalidOtpException ex, HttpServletRequest req
    ){
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ApiError> handleOtpExpired(
        OtpExpiredException ex, HttpServletRequest req
    ){
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(
        UserNotFoundException ex, HttpServletRequest req
    ){
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req){
        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return build(HttpStatus.BAD_REQUEST, message, req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req){
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again.", req.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(
        DataIntegrityViolationException ex, HttpServletRequest req
    ){
        return build(HttpStatus.BAD_REQUEST, "Invalid data provided. Please check the form and try again.", req.getRequestURI());
    }

    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<ApiError> handleServiceNotFound(ServiceNotFoundException ex, HttpServletRequest req){
        return build(HttpStatus.NOT_FOUND,ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ServiceAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleServiceAlreadyExists(ServiceAlreadyExistsException ex, HttpServletRequest req){
        return build(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(PurchaseNotFoundException.class)
    public ResponseEntity<ApiError> handlePurchaseNotFound(PurchaseNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(PurchaseAccessDeniedException.class)
    public ResponseEntity<ApiError> handlePurchaseAccessDenied(PurchaseAccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(InactiveServicePurchaseException.class)
    public ResponseEntity<ApiError> handleInactiveServicePurchase(InactiveServicePurchaseException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(DuplicatePaymentReferenceException.class)
    public ResponseEntity<ApiError> handleDuplicatePaymentReference(DuplicatePaymentReferenceException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "Access denied", req.getRequestURI());
    }
}
