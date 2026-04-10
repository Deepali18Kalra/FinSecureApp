package com.ds.app.exception;
 
public class EmployeeCodeAlreadyExistsException extends Exception {
 
    public EmployeeCodeAlreadyExistsException(String employeeCode) {
        super("Employee code already exists: " + employeeCode);
    }
}
