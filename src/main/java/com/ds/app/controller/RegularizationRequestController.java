package com.ds.app.controller;

import com.ds.app.dto.request.ApprovalRequest;
import com.ds.app.dto.request.RegularizationRequestDTO;
import com.ds.app.dto.response.RegularizationResponse;
import com.ds.app.enums.RegularizationRequestStatus;
import com.ds.app.service.IRegularizationRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/regularizations")
@RequiredArgsConstructor
public class RegularizationRequestController {

    private final IRegularizationRequestService regularizationService;

    // Employee endpoints

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER')")
    @PostMapping
    public ResponseEntity<RegularizationResponse> applyRegularization(
            @Valid @RequestBody RegularizationRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(regularizationService.applyForRegularization(request));
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER')")
    @GetMapping("/my")
    public ResponseEntity<Page<RegularizationResponse>> getMyRegularizationRequests(
            @RequestParam(required = false) RegularizationRequestStatus status,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                regularizationService.getMyRegularizationRequests(status, month, year, pageable)
        );
    }

    // MANAGER endpoints

    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/pending")
    public ResponseEntity<java.util.List<RegularizationResponse>> getPendingRegularizationsForManager() {
        return ResponseEntity.ok(regularizationService.getPendingRegularizationsForManager());
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{requestId}/process")
    public ResponseEntity<RegularizationResponse> processRegularization(
            @PathVariable Long requestId,
            @Valid @RequestBody ApprovalRequest request
    ) {
        return ResponseEntity.ok(regularizationService.processRegularization(requestId, request));
    }
}