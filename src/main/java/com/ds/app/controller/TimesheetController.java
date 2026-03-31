package com.ds.app.controller;

import com.ds.app.dto.ApprovalRequest;
import com.ds.app.dto.TimesheetResponse;
import com.ds.app.service.ITimesheetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timesheets")
@RequiredArgsConstructor
public class TimesheetController {

    private final ITimesheetService timesheetService;

    // Employee
    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
    @GetMapping("/my")
    public ResponseEntity<TimesheetResponse> getMyMonthlyTimesheet(
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(timesheetService.getMyMonthlyTimesheet(month, year));
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
    @PatchMapping("/{timesheetId}/submit")
    public ResponseEntity<TimesheetResponse> submitMyTimesheet(@PathVariable Long timesheetId) {
        return ResponseEntity.ok(timesheetService.submitMyTimesheet(timesheetId));
    }

    // HR
    @PreAuthorize("hasAuthority('HR')")
    @GetMapping("/pending")
    public ResponseEntity<Page<TimesheetResponse>> getPendingTimesheetsForHr(
            @PageableDefault(size = 10, page = 0, sort = "submittedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(timesheetService.getPendingTimesheetsForHr(pageable));
    }

    @PreAuthorize("hasAuthority('HR')")
    @GetMapping("/team")
    public ResponseEntity<Page<TimesheetResponse>> getTeamTimesheetsByMonthYear(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @PageableDefault(size = 10, page = 0, sort = "submittedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(timesheetService.getTeamTimesheetsByMonthYear(month, year, pageable));
    }

    @PreAuthorize("hasAuthority('HR')")
    @PatchMapping("/{timesheetId}/decision")
    public ResponseEntity<TimesheetResponse> reviewTimesheet(
            @PathVariable Long timesheetId,
            @Valid @RequestBody ApprovalRequest request
    ) {
        return ResponseEntity.ok(timesheetService.reviewTimesheet(timesheetId, request));
    }
}