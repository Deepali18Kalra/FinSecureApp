package com.ds.app.validation;

import com.ds.app.dto.request.LeaveRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, LeaveRequest> {
    @Override
    public boolean isValid(LeaveRequest leaveRequest, ConstraintValidatorContext constraintValidatorContext) {
        if(leaveRequest.getStartDate() == null || leaveRequest.getEndDate() == null) {
            return true;
        }
        return !leaveRequest.getEndDate().isBefore(leaveRequest.getStartDate());
    }
}
