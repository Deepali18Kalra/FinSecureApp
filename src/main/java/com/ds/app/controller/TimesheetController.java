package com.ds.app.controller;

import com.ds.app.dto.ApprovalRequest;
import com.ds.app.dto.AttendanceTimesheetDiscrepancyReport;
import com.ds.app.dto.TimesheetResponse;
import com.ds.app.service.ITimesheetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timesheets")
@RequiredArgsConstructor
@Validated
public class TimesheetController {

    private final ITimesheetService timesheetService;

    // Employee endpoints
    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER')")
    @GetMapping("/my")
    public ResponseEntity<TimesheetResponse> getMyMonthlyTimesheet(
            @RequestParam @Min(1) @Max(12) Integer month,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(timesheetService.getMyMonthlyTimesheet(month, year));
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER')")
    @PatchMapping("/{timesheetId}/submit")
    public ResponseEntity<TimesheetResponse> submitMyTimesheet(@PathVariable Long timesheetId) {
        return ResponseEntity.ok(timesheetService.submitMyTimesheet(timesheetId));
    }

    // Manager endpoints
    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/pending")
    public ResponseEntity<Page<TimesheetResponse>> getPendingTimesheetsForManager(
            @PageableDefault(size = 10, page = 0, sort = "submittedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(timesheetService.getPendingTimesheetsForManager(pageable));
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/team")
    public ResponseEntity<Page<TimesheetResponse>> getTeamTimesheetsByMonthYear(
            @RequestParam @Min(1) @Max(12) Integer month,
            @RequestParam Integer year,
            @PageableDefault(size = 10, page = 0, sort = "submittedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(timesheetService.getTeamTimesheetsByMonthYear(month, year, pageable));
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{timesheetId}/decision")
    public ResponseEntity<TimesheetResponse> reviewTimesheet(
            @PathVariable Long timesheetId,
            @Valid @RequestBody ApprovalRequest request
    ) {
        return ResponseEntity.ok(timesheetService.reviewTimesheet(timesheetId, request));
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/reports/discrepancy/{employeeId}")
    public ResponseEntity<AttendanceTimesheetDiscrepancyReport> getAttendanceTimesheetDiscrepancyReport(
            @PathVariable Long employeeId,
            @RequestParam @Min(1) @Max(12) Integer month,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(timesheetService.getAttendanceTimesheetDiscrepancyReport(employeeId, month, year));
    }
}