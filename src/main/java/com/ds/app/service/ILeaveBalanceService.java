package com.ds.app.service;

import com.ds.app.enums.LeaveType;

public interface ILeaveBalanceService {
    void reserveLeaves(Long userId, int year, LeaveType type, int days);
    void releaseReservedLeaves(Long userId, int year, LeaveType type, int days);
    void applyApproval(Long userId, int year, LeaveType type, int days);
    void applyCancellationApproval(Long userId, int year, LeaveType type, int days);
}
