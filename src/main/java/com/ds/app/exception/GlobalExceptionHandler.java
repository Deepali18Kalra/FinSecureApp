
package com.ds.app.exception;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class GlobalExceptionHandler {

   // ── Employee Not Found ─────────────────────────────────────────
   @ExceptionHandler(EmployeeNotFoundException1.class)
   public ResponseEntity<String> handleEmployeeNotFoundException(EmployeeNotFoundException1 ex) {
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
   }

   // ── Employee Code Already Exists ───────────────────────────────
   @ExceptionHandler(EmployeeCodeAlreadyExistsException.class)
   public ResponseEntity<String> handleEmployeeCodeAlreadyExists(EmployeeCodeAlreadyExistsException ex) {
       return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
   }

   // ── Duplicate Email ────────────────────────────────────────────
   @ExceptionHandler(DuplicateEmailException.class)
   public ResponseEntity<String> handleDuplicateEmail(DuplicateEmailException ex) {
       return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
   }

   // ── Duplicate Phone ────────────────────────────────────────────
   @ExceptionHandler(DuplicatePhoneException.class)
   public ResponseEntity<String> handleDuplicatePhone(DuplicatePhoneException ex) {
       return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
   }

   // ── Profile Deleted ────────────────────────────────────────────
   @ExceptionHandler(ProfileDeletedException.class)
   public ResponseEntity<String> handleProfileDeleted(ProfileDeletedException ex) {
       return ResponseEntity.status(HttpStatus.GONE).body(ex.getMessage());
   }

   // ── Profile Already Exists ─────────────────────────────────────
   @ExceptionHandler(ProfileAlreadyExistsException.class)
   public ResponseEntity<String> handleProfileAlreadyExists(ProfileAlreadyExistsException ex) {
       return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
   }

   // ── Account Locked ─────────────────────────────────────────────
   @ExceptionHandler(AccountLockedException.class)
   public ResponseEntity<String> handleAccountLocked(AccountLockedException ex) {
       return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
   }

   // ── File Storage ───────────────────────────────────────────────
   @ExceptionHandler(FileStorageException.class)
   public ResponseEntity<String> handleFileStorage(FileStorageException ex) {
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
   }

   


   // ── Generic Exception ──────────────────────────────────────────
   @ExceptionHandler(Exception.class)
   public ResponseEntity<String> handleException(Exception ex) {
       return ResponseEntity
               .status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body(ex.getMessage());
   }
	 
}//end class
