package com.ds.app.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ds.app.dto.request.*;
import com.ds.app.dto.response.*;
import com.ds.app.service.EmployeePhotoService;
import com.ds.app.service.EmployeeSearchService;
import com.ds.app.service.impl.EmployeePhotoServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/finsecure/hr/employee")
@Validated
@Tag(name = "Employee Search & Reports", description = "HR/Admin read-only search and reports. Use /search to find userId before calling update or delete.")
public class EmployeeSearchController {
	
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeePhotoServiceImpl.class);
	
	@Autowired
	EmployeeSearchService employeeSearchService;
		
	   @GetMapping("/search")
	    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
	    @Operation(
	        summary = "Search and filter employees (HR / Admin)",
	        description = "All filter fields are optional. " +
	                      "Pagination: ?page=0&size=10&sort=createdAt,desc. " +
	                      "Primary use: find userId from results, then call HR update or Admin delete."
	    )
	    public ResponseEntity<PagedResponseDTO<EmployeeProfileResponseDTO>> searchEmployees(@ParameterObject
	            EmployeeFilterRequestDTO filter,@ParameterObject
	            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
	            Pageable pageable) {
	        logger.info("Search — filter: {}, pageable: {}", filter, pageable);
	        return ResponseEntity.ok(employeeSearchService.filterUsers(filter, pageable));
	    }
	   
	   @GetMapping("/count/report")
	    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
	    @Operation(
	        summary = "Employee count report (HR / Admin)",
	        description = "Returns counts grouped by various criteria:\n\n" +
	                      "- **totalActive** — employees with isDeleted = false\n" +
	                      "- **totalDeleted** — employees with isDeleted = true\n" +
	                      "- **totalFreshers** — employees with experience = FRESHER\n" +
	                      "- **totalExperienced** — employees with experience = EXPERIENCED\n" +
	                      "- **totalCertified** — employees with certificationStatus = CERTIFIED\n" +
	                      "- **totalNonCertified** — employees with certificationStatus = NON_CERTIFIED\n" 
      
	    )
	    public ResponseEntity<CountReportDTO> getCountReport() {
	        logger.info("Count report requested");
	        return ResponseEntity.ok(employeeSearchService.getCountReport());
	    }
	 

	    @GetMapping("/recent")
	    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
	    @Operation(
	        summary = "Recently joined employees (HR / Admin)",
	        description = "Returns employees who joined in the last N days.\n\n" +
	                      "**How to use:**\n" +
	                      "- `?days=7`   → joined in last 7 days\n" +
	                      "- `?days=30`  → joined in last 30 days (default)\n" +
	                      "- `?days=90`  → joined in last 3 months\n" +
	                      "- `?days=365` → joined in last 1 year\n\n" +
	                      "Results sorted by joining date — newest first.\n" +
	                      "Email and phone are masked in response."
	    )
	    public ResponseEntity<PagedResponseDTO<EmployeeProfileResponseDTO>> getRecentlyJoined(
	            @RequestParam(defaultValue = "30") int days,
	            @ParameterObject
	            @PageableDefault(page = 0, size = 10,
	                             sort = "userId",
	                             direction = Sort.Direction.DESC)
	            Pageable pageable) {
	 
	        logger.info("Recently joined — last {} days", days);
	        return ResponseEntity.ok(employeeSearchService.getRecentlyJoined(days, pageable));
	    }
	    
	    @GetMapping("/no-photo")
	    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
	    @Operation(
	        summary = "Employees without profile photo (HR / Admin)",
	        description = "Returns employees who have not uploaded a profile photo yet.\n\n" +
	                      "HR uses this to:\n" +
	                      "- See who is missing a photo\n" +
	                      "- Send reminders to upload photo\n\n" +
	                      "Email and phone are masked in response.\n" +
	                      "Pagination: ?page=0&size=10&sort=userId,desc"
	    )
	    public ResponseEntity<PagedResponseDTO<EmployeeSimpleResponseDTO>> getEmployeesWithoutPhoto(
	            @ParameterObject @PageableDefault(
	                page = 0, size = 10, sort = "userId",direction = Sort.Direction.DESC
	            ) Pageable pageable) {
	     
	        logger.info("Employees without photo requested");
	        return ResponseEntity.ok(employeeSearchService.getEmployeesWithoutPhoto(pageable));
	    }
	    @GetMapping("/incomplete")
	    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
	    @Operation(
	        summary = "Filter incomplete profiles (HR / Admin)",
	        description =
	            "Returns employees who have NOT filled their personal info.\n\n" +
	            "isProfileComplete will be false for all results.\n" +
	            "Pagination: ?page=0&size=10&sort=userId,desc"
	    )
	    public ResponseEntity<PagedResponseDTO<EmployeeIncompleteResponseDTO>> getIncompleteProfiles(@ParameterObject 
	    		@PageableDefault(page = 0,size = 10, sort = "userId", direction = Sort.Direction.DESC)
	            Pageable pageable) {

	        logger.info("Incomplete profiles requested");
	        
	        return ResponseEntity.ok(employeeSearchService.getIncompleteProfiles(pageable));
	    }
	    
	    
	    @GetMapping("/stats/monthly")
	    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
	    @Operation(
	    	summary = "Monthly joining statistics (HR / Admin)",
	        description =
	            "Returns employee joining count for each month of a given year.\n\n" +
	            "**How to use:**\n" +
	            "- `?year=2024` → monthly breakdown for 2024\n" +
	            "- `?year=2023` → monthly breakdown for 2023\n" +
	            "- Default: current year\n\n" +
	            "All 12 months are always shown.\n" +
	            "Months with no joinings show count = 0."
	    )
	    public ResponseEntity<List<MonthlyStatDTO>> getMonthlyStats( @RequestParam(defaultValue = "#{T(java.time.LocalDate)" + ".now().getYear()}") int year) {
	     
	        logger.info("Monthly stats for year: {}", year);
	        return ResponseEntity.ok(employeeSearchService.getMonthlyStats(year));
	    }
	     
	    
	    @GetMapping("/stats/yearly")
	    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
	    @Operation(
	    		summary = "Yearly joining statistics (HR / Admin)", description =
	            "Returns employee joining count for each year.\n\n" +
	            "No params needed — returns all years automatically.\n" +
	            "Sorted by year descending — most recent year first.")
	    
	    public ResponseEntity<List<YearlyStatDTO>> getYearlyStats() {
	    	
	    logger.info("Yearly stats requested");
	        
	    return ResponseEntity.ok(employeeSearchService.getYearlyStats());
	        
	    }
	     

}//ENDCLASS
