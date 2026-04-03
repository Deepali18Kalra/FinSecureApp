package com.ds.app.controller;

import com.ds.app.dto.HolidayRequest;
import com.ds.app.dto.HolidayResponse;
import com.ds.app.service.IHolidayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final IHolidayService holidayService;

    // ── HR only ──

    @PreAuthorize("hasAuthority('HR')")
    @PostMapping
    public ResponseEntity<HolidayResponse> createHoliday(
            @Valid @RequestBody HolidayRequest request) {
        HolidayResponse response = holidayService.createHoliday(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('HR')")
    @DeleteMapping("/{holidayId}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long holidayId) {
        holidayService.deleteHoliday(holidayId);
        return ResponseEntity.noContent().build();
    }

    // ── Everyone (EMPLOYEE, MANAGER, HR, ADMIN) ──

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER','HR','ADMIN')")
    @GetMapping
    public ResponseEntity<List<HolidayResponse>> getHolidaysByYear(
            @RequestParam(required = false) Integer year) {
        List<HolidayResponse> response = holidayService.getHolidaysByYear(year);
        return ResponseEntity.ok(response);
    }
}