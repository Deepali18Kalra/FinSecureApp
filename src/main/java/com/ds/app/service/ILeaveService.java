package com.ds.app.service;

import com.ds.app.dto.request.ApprovalRequest;
import com.ds.app.dto.request.LeaveRequest;
import com.ds.app.dto.response.LeaveResponse;
import com.ds.app.dto.response.LeaveStatusResponse;
import com.ds.app.enums.LeaveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ILeaveService {
    // Employee related methods
    LeaveResponse applyLeave(LeaveRequest leaveRequest);

    Page<LeaveStatusResponse> getMyLeaves(LeaveStatus status, Integer year, Integer month, Pageable pageable);

    LeaveResponse cancelOrWithdrawLeave(Long leaveId);
    
    // MANAGER related methods
    LeaveResponse processLeaveRequest(Long leaveId, ApprovalRequest approvalRequest);

    LeaveResponse processCancellationRequest(Long leaveId, ApprovalRequest approvalRequest);

    Page<LeaveResponse> getPendingRequest(Pageable pageable);
}