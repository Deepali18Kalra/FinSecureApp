package com.ds.app.controller;

import com.ds.app.dto.response.LeaveBalanceResponse;
import com.ds.app.service.ILeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-balance")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final ILeaveBalanceService leaveBalanceService;

    // Employee endpoints

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER')")
    @GetMapping("/my")
    public ResponseEntity<LeaveBalanceResponse> getMyLeaveBalance(
            @RequestParam(required = false) Integer year) {
        LeaveBalanceResponse response = leaveBalanceService.getMyLeaveBalance(year);
        return ResponseEntity.ok(response);
    }

    // Manager endpoints

    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<LeaveBalanceResponse> getEmployeeLeaveBalance(
            @PathVariable Long employeeId,
            @RequestParam(required = false) Integer year) {
        LeaveBalanceResponse response = leaveBalanceService
                .getEmployeeLeaveBalance(employeeId, year);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/team")
    public ResponseEntity<List<LeaveBalanceResponse>> getTeamLeaveBalances(
            @RequestParam(required = false) Integer year) {
        List<LeaveBalanceResponse> response = leaveBalanceService
                .getTeamLeaveBalances(year);
        return ResponseEntity.ok(response);
    }
}