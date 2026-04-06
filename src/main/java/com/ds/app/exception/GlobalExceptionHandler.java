package com.ds.app.exception;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;


@ControllerAdvice
public class GlobalExceptionHandler {
	
	 @ExceptionHandler(EmployeeNotFoundException.class)
	    public ResponseEntity<String> handleEmployeeNotFound(EmployeeNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	    }
	 
	  @ExceptionHandler(TrainingNotFoundException.class)
	    public ResponseEntity<String> handleTrainingNotFound(TrainingNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	    }

	    @ExceptionHandler(TrainingNotCompleteException.class)
	    public ResponseEntity<String> handleTrainingNotCompleted(TrainingNotCompleteException ex) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	    }


    // ✅ 1. Validation Errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            MethodArgumentNotValidException ex) {


        Map<String, String> errors = new HashMap<>();


        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }


        ApiResponse<Object> response = new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                errors
        );


        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    // ✅ 2. Custom Exception (Business logic)
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException ex) {


        ApiResponse<Object> response = new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                null
        );


        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    // ✅ 3. Generic Exception (Fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {


        ApiResponse<Object> response = new ApiResponse<>(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Something went wrong",
                null
        );


        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}



