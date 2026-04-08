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
@RequestMapping("/finsecure/reports")
public class AssetReportController {

    private final AssetReportService assetReportService;
	private final AssetSearchReportService assetSearchReportService;
	private final AssetUtilizationReportService utilizationReportService;
    private final EmployeeReportService employeeReportService;
	private final EscalatedAssetReportService escalatedAssetReportService;

    private final iAppUserRepository appUserRepository;

    private final AssetRepository assetRepository;
    private final AssetAllocationRepository allocationRepository;
    private final AssetIssueRepository issueRepository;
    private final AssetEscalationRepository escalationRepository;
    

	public AssetReportController(AssetReportService assetReportService,
			AssetSearchReportService assetSearchReportService, AssetUtilizationReportService utilizationReportService,
			EmployeeReportService employeeReportService, EscalatedAssetReportService escalatedAssetReportService,
			iAppUserRepository appUserRepository, AssetRepository assetRepository,
			AssetAllocationRepository allocationRepository, AssetIssueRepository issueRepository,
			AssetEscalationRepository escalationRepository) {
		super();
		this.assetReportService = assetReportService;
		this.assetSearchReportService = assetSearchReportService;
		this.utilizationReportService = utilizationReportService;
		this.employeeReportService = employeeReportService;
		this.escalatedAssetReportService = escalatedAssetReportService;
		this.appUserRepository = appUserRepository;
		this.assetRepository = assetRepository;
		this.allocationRepository = allocationRepository;
		this.issueRepository = issueRepository;
		this.escalationRepository = escalationRepository;
	}



//	@GetMapping("/assets/escalations")
//    @PreAuthorize("hasAuthority('EMPLOYEE')")
//    public ResponseEntity<?> getMyEscalations(Principal principal) {
//
//        AppUser user = appUserRepository.findByUsername(principal.getName())
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        List<EscalationReportDTO> escalations =
//                assetReportService.getEscalationsForEmployee(user.getUserId());
//
//        if (escalations.isEmpty()) {
//            return ResponseEntity.ok("No active escalations for this employee");
//        }
//
//        return ResponseEntity.ok(escalations);
//    }
//    
    
    
    @GetMapping("/assets/escalations/date")
       public List<EscalationReportDTO> getEscalationsOnDate(
               @RequestParam
               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
               LocalDate date) {

           return assetReportService.getEscalationsOnDate(date);
       }

    

@GetMapping("/assets/escalations/range")
    public List<EscalationReportDTO> getEscalationsBetweenDates(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to) {

        return assetReportService.getEscalationsBetweenDates(from, to);
    }

    
    @GetMapping("/assets/{assetId}/availability")
    public AssetAvailabilityReportDTO getAssetAvailabilityReport(
            @PathVariable Long assetId) {

        return assetReportService.getAssetAvailabilityReport(assetId);
    }
    
    
    @GetMapping("/assets/search")
	public List<AssetSearchResultDTO> searchAssets(@RequestParam String keyword)
	{
		return assetSearchReportService.searchAssets(keyword);
	}
    
    
    @GetMapping("/assets/{assetId}/utilization")
	public AssetUtilizationReportDTO getAssetUtilization(@PathVariable Long assetId) {
		return utilizationReportService.getAssetUtilization(assetId);
	}
    
    @GetMapping("/employees/{employeeId}/assets")
    public EmployeeAssetHoldingReportDTO getAssetsHeldByEmployee(
            @PathVariable Long employeeId) {

        return employeeReportService.getAssetsHeldByEmployee(employeeId);
    }


	@GetMapping("/assets/{assetId}/escalations")
	public List<EscalatedAssetReportDTO> getEscalationAwareAssetReport(@PathVariable Long assetId)
	{
		
		return escalatedAssetReportService.getEscalationAwareAssetReport(assetId);
	}
    
	@GetMapping("/assets/count")
    public Map<String, Long> getTotalAssetsCount() {
        Map<String, Long> res = new HashMap<>();
        res.put("totalAssets", assetRepository.count());
        return res;
    }

    @GetMapping("/assets/issues/count")
    public Map<String, Long> getTotalIssuesCount() {
        Map<String, Long> res = new HashMap<>();
        res.put("totalIssues", issueRepository.count());
        return res;
    }

    @GetMapping("/assets/escalations/count")
    public Map<String, Long> getTotalEscalationsCount() {
        Map<String, Long> res = new HashMap<>();
        res.put("totalEscalations", escalationRepository.count());
        return res;
    }

 
    @GetMapping("/assets/units/summary")
    public Map<String, Integer> getAssetUnitsSummary() {

        int totalUnits = 0;
        int allocatedUnits = 0;
        int availableUnits = 0;

        for (Asset asset : assetRepository.findAll()) {
        	if (asset.getTotalUnits() != null)
        		totalUnits += asset.getTotalUnits();

        	if (asset.getAllocatedUnits() != null)
        		allocatedUnits += asset.getAllocatedUnits();

        	if (asset.getAvailableUnits() != null)
        		availableUnits += asset.getAvailableUnits();
        }

        Map<String, Integer> res = new HashMap<>();
        res.put("totalUnits", totalUnits);
        res.put("allocatedUnits", allocatedUnits);
        res.put("availableUnits", availableUnits);

        return res;
    }

 
    @GetMapping("/assets/status-count")
    public Map<String, Long> getAssetsByStatus() {

        Map<String, Long> res = new HashMap<>();

        assetRepository.findAll().forEach(asset -> {
            String status = asset.getStatus().name();
            res.put(status, res.getOrDefault(status, 0L) + 1);
        });

        return res;
    }

    @GetMapping("/assets/issues/status-count")
    public Map<String, Long> getIssuesByStatus() {

        Map<String, Long> res = new HashMap<>();

        issueRepository.findAll().forEach(issue -> {
            String status = issue.getStatus().name();
            res.put(status, res.getOrDefault(status, 0L) + 1);
        });

        return res;
    }

    @GetMapping("/assets/escalations/status-count")
    public Map<String, Long> getEscalationsByStatus() {

        Map<String, Long> res = new HashMap<>();

        escalationRepository.findAll().forEach(esc -> {
            String status = esc.getStatus().name();
            res.put(status, res.getOrDefault(status, 0L) + 1);
        });

        return res;
    }

 
    @GetMapping("/assets/units/detail")
    public List<Map<String, Object>> getAssetUnitsDetail() {

        List<Map<String, Object>> result = new ArrayList<>();

        assetRepository.findAll().forEach(asset -> {
            Map<String, Object> row = new HashMap<>();
            row.put("assetId", asset.getAssetId());
            row.put("assetName", asset.getName());
            row.put("totalUnits", asset.getTotalUnits());
            row.put("allocatedUnits", asset.getAllocatedUnits());
            row.put("availableUnits", asset.getAvailableUnits());
            result.add(row);
        });

        return result;
    }

 
    @GetMapping("/assets/allocations/per-employee")
    public List<Map<String, Object>> getAllocationsPerEmployee() {

        List<Map<String, Object>> result = new ArrayList<>();

        allocationRepository.findAll().forEach(allocation -> {
            Map<String, Object> row = new HashMap<>();
            row.put("employeeId", allocation.getEmployee().getUserId());
            row.put("assetId", allocation.getAsset().getAssetId());
            row.put("assetName", allocation.getAsset().getName());
            row.put("allocatedDate", allocation.getAllocatedDate());
            row.put("deallocatedDate", allocation.getDeallocatedDate());
            row.put("status", allocation.getStatus().name());
            result.add(row);
        });

        return result;
    }
       
    
    
    
    
    
    
    
    
    
}
