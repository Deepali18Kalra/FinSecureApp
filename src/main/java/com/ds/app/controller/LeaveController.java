package com.ds.app.controller;

import com.ds.app.dto.request.ApprovalRequest;
import com.ds.app.dto.request.LeaveRequest;
import com.ds.app.dto.response.LeaveResponse;
import com.ds.app.dto.response.LeaveStatusResponse;
import com.ds.app.enums.LeaveStatus;
import com.ds.app.service.ILeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final ILeaveService leaveService;

    // Employee endpoints
    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER')")
    @PostMapping
    public ResponseEntity<LeaveResponse> applyLeave(@RequestBody LeaveRequest leaveRequest) {
        LeaveResponse response = leaveService.applyLeave(leaveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'MANAGER')")
    @GetMapping
    public ResponseEntity<Page<LeaveStatusResponse>> getMyLeaves(
    		@RequestParam(required = false) LeaveStatus status,
    		@RequestParam(required = false) Integer year,
    		@RequestParam(required = false) Integer month,
            @PageableDefault(size = 10, page = 0, sort = "leaveId", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<LeaveStatusResponse> pageResponse = leaveService.getMyLeaves(status, year, month, pageable);
        return ResponseEntity.ok(pageResponse);
    }
    
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'MANAGER')")
    @PatchMapping("/{leaveId}/cancel")
    public ResponseEntity<LeaveResponse> cancelOrWithdraw(
    		@PathVariable Long leaveId
    		) {
    	LeaveResponse response = leaveService.cancelOrWithdrawLeave(leaveId);
    	return ResponseEntity.ok(response);
    }

    // MANAGER endpoints
    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{leaveId}/process")
    public ResponseEntity<LeaveResponse> processLeaveRequest(
            @PathVariable Long leaveId,
            @RequestBody ApprovalRequest approvalRequest
    ) {
        LeaveResponse response = leaveService.processLeaveRequest(leaveId, approvalRequest);
        return ResponseEntity.ok(response);
    }
    
    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{leaveId}/process-cancel-request")
    public ResponseEntity<LeaveResponse> processCancellationRequest(
    		@PathVariable Long leaveId,
    		@RequestBody ApprovalRequest approvalRequest
    		) {
    	LeaveResponse response = leaveService.processCancellationRequest(leaveId, approvalRequest);
    	return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/pending")
    public ResponseEntity<Page<LeaveResponse>> getPendingRequests(
            @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<LeaveResponse> response = leaveService.getPendingRequest(pageable);
        return ResponseEntity.ok(response);
    }
}