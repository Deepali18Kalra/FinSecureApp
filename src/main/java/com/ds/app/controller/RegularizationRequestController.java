package com.ds.app.controller;

import java.util.List;

import com.ds.app.service.IRegularizationRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ds.app.dto.ApprovalRequest;
import com.ds.app.dto.RegularizationRequestdto;
import com.ds.app.dto.RegularizationResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/regularizations")
@RequiredArgsConstructor
public class RegularizationRequestController {

    private final IRegularizationRequestService regularizationService;

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
    @PostMapping
    public ResponseEntity<RegularizationResponse> applyRegularization(
            @Valid @RequestBody RegularizationRequestdto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(regularizationService.applyRegularization(request));
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','HR')")
    @GetMapping("/my")
    public ResponseEntity<List<RegularizationResponse>> getMyRegularizationRequests(
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(regularizationService.getMyRegularizationRequests(status));
    }

    @PreAuthorize("hasAuthority('HR')")
    @GetMapping("/pending")
    public ResponseEntity<List<RegularizationResponse>> getPendingRegularizationsForHr() {
        return ResponseEntity.ok(regularizationService.getPendingRegularizationsForHr());
    }

    @PreAuthorize("hasAuthority('HR')")
    @PatchMapping("/{requestId}/decision")
    public ResponseEntity<RegularizationResponse> reviewRegularization(
            @PathVariable Long requestId,
            @Valid @RequestBody ApprovalRequest request
    ) {
        return ResponseEntity.ok(regularizationService.reviewRegularization(requestId, request));
    }
}