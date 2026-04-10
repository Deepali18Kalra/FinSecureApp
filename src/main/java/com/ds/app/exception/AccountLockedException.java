package com.ds.app.exception;
 
public class AccountLockedException extends Exception {
 
    public AccountLockedException(String userId) {
        super("Account is locked for userId: " + userId + ". Contact Admin to unlock.");
    }
}
 
