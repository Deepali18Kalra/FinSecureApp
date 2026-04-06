package com.ds.app.controller;

import com.ds.app.dto.ApprovalRequest;
import com.ds.app.dto.RegularizationRequestDTO;
import com.ds.app.dto.RegularizationResponse;
import com.ds.app.enums.RegularizationRequestStatus;
import com.ds.app.service.IRegularizationRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.status(HttpStatus.CREATED).body(regularizationService.applyForRegularization(request));
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER')")
    @GetMapping("/my")
    public ResponseEntity<List<RegularizationResponse>> getMyRegularizationRequests(
            @RequestParam(required = false) RegularizationRequestStatus status
    ) {
        return ResponseEntity.ok(regularizationService.getMyRegularizationRequests(status));
    }

    // MANAGER endpoints
    
    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/pending")
    public ResponseEntity<List<RegularizationResponse>> getPendingRegularizationsForManager() {
        return ResponseEntity.ok(regularizationService.getPendingRegularizationsForManager());
    }

    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping("/{requestId}/decision")
    public ResponseEntity<RegularizationResponse> reviewRegularization(
            @PathVariable Long requestId,
            @Valid @RequestBody ApprovalRequest request
    ) {
        return ResponseEntity.ok(regularizationService.processRegularization(requestId, request));
    }
}