package com.ds.app.exception;
 
public class ProfileDeletedException extends Exception {
 
    public ProfileDeletedException(Long userId) {
        super("Employee profile is deleted for userId: "
                + userId + ". Contact HR to restore.");
    }
}