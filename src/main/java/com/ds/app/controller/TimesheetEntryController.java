package com.ds.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ds.app.dto.TimesheetEntryRequest;
import com.ds.app.dto.TimesheetEntryResponse;
import com.ds.app.service.ITimesheetEntryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/timesheet-entries")
@RequiredArgsConstructor
public class TimesheetEntryController {

    private final ITimesheetEntryService service;

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
    @PostMapping
    public ResponseEntity<TimesheetEntryResponse> addMyEntry(@Valid @RequestBody TimesheetEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addMyEntry(request));
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
    @GetMapping
    public ResponseEntity<List<TimesheetEntryResponse>> getMyEntries(
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(service.getMyEntries(month, year));
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
    @GetMapping("/range")
    public ResponseEntity<List<TimesheetEntryResponse>> getMyEntriesByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(service.getMyEntriesByDateRange(startDate, endDate));
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
    @PutMapping("/{entryId}")
    public ResponseEntity<TimesheetEntryResponse> updateMyEntry(
            @PathVariable Long entryId,
            @Valid @RequestBody TimesheetEntryRequest request
    ) {
        return ResponseEntity.ok(service.updateMyEntry(entryId, request));
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> deleteMyEntry(@PathVariable Long entryId) {
        service.deleteMyEntry(entryId);
        return ResponseEntity.noContent().build();
    }
}