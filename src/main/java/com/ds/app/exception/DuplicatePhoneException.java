package com.ds.app.exception;
 
public class DuplicatePhoneException extends Exception {
 
    public DuplicatePhoneException(String phoneNumber) {
        super("Phone number already exists: " + phoneNumber);
    }
}
