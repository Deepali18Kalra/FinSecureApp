package com.ds.app.service;

import com.ds.app.dto.ApprovalRequest;
import com.ds.app.dto.LeaveRequest;
import com.ds.app.dto.LeaveResponse;
import com.ds.app.dto.LeaveStatusResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ILeaveService {
    // Employee related methods
    LeaveResponse applyLeave(LeaveRequest leaveRequest);
    LeaveStatusResponse getLeaveStatus(Long leaveId);

    // HR related methods
    LeaveResponse reviewLeave(Long leaveId, ApprovalRequest approvalRequest);
    Page<LeaveResponse> getPendingRequest(Pageable pageable);
}