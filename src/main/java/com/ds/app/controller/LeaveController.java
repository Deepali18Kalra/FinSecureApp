package com.ds.app.controller;

import com.ds.app.dto.ApprovalRequest;
import com.ds.app.dto.LeaveRequest;
import com.ds.app.dto.LeaveResponse;
import com.ds.app.dto.LeaveStatusResponse;
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
    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
    @PostMapping
    public ResponseEntity<LeaveResponse> applyLeave(@RequestBody LeaveRequest leaveRequest) {
        LeaveResponse response = leaveService.applyLeave(leaveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
    @GetMapping("/{leaveId}/status")
    public ResponseEntity<LeaveStatusResponse> getLeaveStatus(@PathVariable Long leaveId) {
        LeaveStatusResponse response = leaveService.getLeaveStatus(leaveId);
        return ResponseEntity.ok(response);
    }

    // HR endpoints
    @PreAuthorize("hasAuthority('HR')")
    @PatchMapping("/{leaveId}/decision")
    public ResponseEntity<LeaveResponse> reviewLeave(
            @PathVariable Long leaveId,
            @RequestBody ApprovalRequest approvalRequest
    ) {
        LeaveResponse response = leaveService.reviewLeave(leaveId, approvalRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('HR')")
    @GetMapping("/pending")
    public ResponseEntity<Page<LeaveResponse>> getPendingRequests(
            @PageableDefault(size = 10, page = 0, direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<LeaveResponse> response = leaveService.getPendingRequest(pageable);
        return ResponseEntity.ok(response);
    }
}