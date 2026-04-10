package com.ds.app.exception;
 
public class ProfileAlreadyExistsException extends Exception {
 
    public ProfileAlreadyExistsException(String username) {
        super("Profile already exists for username: " + username);
    }
}
