package com.ds.app.controller;

import com.ds.app.dto.TimesheetEntryRequest;
import com.ds.app.dto.TimesheetEntryResponse;
import com.ds.app.service.ITimesheetEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/timesheet-entries")
@RequiredArgsConstructor
public class TimesheetEntryController {

    private final ITimesheetEntryService service;

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER')")
    @PostMapping
    public ResponseEntity<TimesheetEntryResponse> addMyEntry(@Valid @RequestBody TimesheetEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addMyEntry(request));
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER')")
    @GetMapping
    public ResponseEntity<List<TimesheetEntryResponse>> getMyEntries(
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(service.getMyEntries(month, year));
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER')")
    @GetMapping("/range")
    public ResponseEntity<List<TimesheetEntryResponse>> getMyEntriesByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(service.getMyEntriesByDateRange(startDate, endDate));
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER')")
    @PutMapping("/{entryId}")
    public ResponseEntity<TimesheetEntryResponse> updateMyEntry(
            @PathVariable Long entryId,
            @Valid @RequestBody TimesheetEntryRequest request
    ) {
        return ResponseEntity.ok(service.updateMyEntry(entryId, request));
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER')")
    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> deleteMyEntry(@PathVariable Long entryId) {
        service.deleteMyEntry(entryId);
        return ResponseEntity.noContent().build();
    }
}