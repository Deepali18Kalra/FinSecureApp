package com.ds.app.enums;

public enum SalarySkipReason {
	
    ALREADY_PAID,           // salary already credited this month
    
    NO_BANK_ACCOUNT,        // employee has no registered bank account
    
    BANK_ACCOUNT_PENDING,   // bank account not yet approved by Finance
    
    BANK_ACCOUNT_REJECTED,  // bank account was rejected by Finance
    
    BANK_BLACKLISTED,       // employee's bank was blacklisted after account was added
    
    EMPLOYEE_INACTIVE,      // employee status is INACTIVE
    
    PROCESSING_ERROR,
    
    EMAIL_FAILURE   // unexpected exception during processing
    
    
}
