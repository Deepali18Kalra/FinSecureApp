package com.ds.app.exception;
import com.ds.app.entity.SalaryJob;
import com.ds.app.enums.BankStatus;
import com.ds.app.enums.FundStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BlacklistedBankException.class)
    public ResponseEntity<String> handleBlacklistedFundException(BlacklistedBankException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleDuplicatePrimaryAccountException(ResourceNotFoundException ex) {
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(SalaryJobException.class)
    public ResponseEntity<String> handleDuplicatePrimaryAccountException(SalaryJobException ex) {
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(BankAccountAlreadyRegistered.class)
    public ResponseEntity<String> BankAccountAlreadyRegistered(SalaryJobException ex) {
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<String> ResourceAlreadyExistException(SalaryJobException ex) {
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    
    

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleEnumMismatch(MethodArgumentTypeMismatchException ex) {

        String paramName  = ex.getName();
        String invalidValue = String.valueOf(ex.getValue());
        String message;

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Object[] enumValues = ex.getRequiredType().getEnumConstants();
            message = "Invalid value '" + invalidValue + "' for '" + paramName + "'. Accepted values: " + Arrays.toString(enumValues);
        } else {
            message = "Invalid value '" + invalidValue + "' for parameter '" + paramName + "'";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }


}
