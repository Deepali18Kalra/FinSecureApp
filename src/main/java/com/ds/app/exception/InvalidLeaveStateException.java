package com.ds.app.exception;

public class InvalidLeaveStateException extends RuntimeException {
    public InvalidLeaveStateException(String message) {
        super(message);
    }
}