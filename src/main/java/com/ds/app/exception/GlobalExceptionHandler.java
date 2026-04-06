package com.ds.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BlacklistedBankException.class)
    public ResponseEntity<String> handleBlacklistedBankException(BlacklistedBankException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(SalaryJobException.class)
    public ResponseEntity<String> handleSalaryJobException(SalaryJobException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(BankAccountAlreadyRegistered.class)
    public ResponseEntity<String> handleBankAccountAlreadyRegistered(BankAccountAlreadyRegistered ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<String> handleResourceAlreadyExist(ResourceAlreadyExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(BankAccountLockedException.class)
    public ResponseEntity<String> handleBankAccountLocked(BankAccountLockedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // ✅ Spring Security 6 needs BOTH together
    @ExceptionHandler({
            AccessDeniedException.class,
            AuthorizationDeniedException.class
    })
    public ResponseEntity<Map<String, String>> handleAccessDenied(Exception ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", "Access Denied - You don't have permission");

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleEnumMismatch(MethodArgumentTypeMismatchException ex) {

        String paramName = ex.getName();
        String invalidValue = String.valueOf(ex.getValue());
        String message;

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Object[] enumValues = ex.getRequiredType().getEnumConstants();
            message = "Invalid value '" + invalidValue + "' for '" + paramName + "'. Accepted values: "
                    + Arrays.toString(enumValues);
        } else {
            message = "Invalid value '" + invalidValue + "' for parameter '" + paramName + "'";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    // ✅ ONLY ONE Generic Exception handler allowed
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneral(Exception ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}