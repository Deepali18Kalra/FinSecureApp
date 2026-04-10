package com.ds.app.exception;

public class EmployeeNotFoundException1 extends Exception {

    public EmployeeNotFoundException1(String username) {
        super("Employee not found for username: " + username);
    }

    public EmployeeNotFoundException1(Long userId) {
        super("Employee not found for userId: " + userId);
    }
}
