package com.ds.app.exception;
 
public class DuplicateEmailException extends Exception {
 
    public DuplicateEmailException(String email) {
        super("Email already exists: " + email);
    }
}
