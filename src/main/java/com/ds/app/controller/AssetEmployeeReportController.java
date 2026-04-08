package com.ds.app.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ds.app.dto.AssetAvailabilityReportDTO;
import com.ds.app.dto.AssetSearchResultDTO;
import com.ds.app.dto.AssetUtilizationReportDTO;
import com.ds.app.dto.EmployeeAssetHoldingReportDTO;
import com.ds.app.dto.EscalatedAssetReportDTO;
import com.ds.app.dto.EscalationReportDTO;
import com.ds.app.entity.AppUser;
import com.ds.app.entity.Asset;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.AssetAllocationRepository;
import com.ds.app.repository.AssetEscalationRepository;
import com.ds.app.repository.AssetIssueRepository;
import com.ds.app.repository.AssetRepository;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.service.AssetReportService;
import com.ds.app.service.AssetSearchReportService;
import com.ds.app.service.AssetUtilizationReportService;
import com.ds.app.service.EmployeeReportService;
import com.ds.app.service.EscalatedAssetReportService;

@RestController
@RequestMapping("/finsecure/employee")
public class AssetEmployeeReportController {

    private final AssetReportService assetReportService;

    private final iAppUserRepository appUserRepository;
	public AssetEmployeeReportController(AssetReportService assetReportService, iAppUserRepository appUserRepository) {
		super();
		this.assetReportService = assetReportService;
		this.appUserRepository = appUserRepository;
	}

	@GetMapping("/assets/escalations")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<?> getMyEscalations(Principal principal) {

        AppUser user = appUserRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<EscalationReportDTO> escalations =
                assetReportService.getEscalationsForEmployee(user.getUserId());

        if (escalations.isEmpty()) {
            return ResponseEntity.ok("No active escalations for this employee");
        }

        return ResponseEntity.ok(escalations);
    }
    
    
    
    }
