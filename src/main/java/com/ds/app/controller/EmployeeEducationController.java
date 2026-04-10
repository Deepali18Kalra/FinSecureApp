package com.ds.app.controller;
 
import java.util.List;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ds.app.dto.request.EmployeeEducationRequestDTO;
import com.ds.app.dto.response.EmployeeEducationResponseDTO;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.EmployeeEducationService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
 
@RestController
@Tag(name = "Education", description = "Employee education history management")
public class EmployeeEducationController {
 
    private static final Logger logger =LoggerFactory.getLogger(EmployeeEducationController.class);
 
    @Autowired 
    EmployeeEducationService emEducationService;
    
    @Autowired
    EmployeeRepository iEmployeeRepo;
 
	    private Long getUserId(String username) {
	    	
	        return iEmployeeRepo.findByUsername(username)
	                .orElseThrow(() -> new RuntimeException( "Employee not found: " + username))
	                .getUserId();
	    }
	 
	    @PostMapping("/finsecure/employee/education")
	    @PreAuthorize("hasAuthority('EMPLOYEE')")
	    @Operation( summary = "Add education record (Employee)",description =
	            "Employee adds a new education record.\n\n" +
	            "One employee can add multiple records:\n" +
	            "e.g. B.Tech + MBA = 2 separate records.\n\n" +
	            "**Required:** degree, institution\n" +
	            "fieldOfStudy, passingYear, " +
	            "percentage, grade, location")
	    
	    public ResponseEntity<EmployeeEducationResponseDTO> addEducation( @Valid @RequestBody EmployeeEducationRequestDTO dto, 
	    		@AuthenticationPrincipal UserDetails userDetails) throws Exception {
	 
	    Long userId = getUserId(userDetails.getUsername());
	    logger.info("Add education for: {}", userDetails.getUsername());
	    return new ResponseEntity<>(emEducationService.addEducation(dto, userId), HttpStatus.CREATED);
	    }
	 
	    @GetMapping("/finsecure/employee/education")
	    @PreAuthorize("hasAuthority('EMPLOYEE')")
	    @Operation(summary = "Get my education records (Employee)", description =
	            "Returns all education records for logged in employee.\n" +
	            "Sorted by passing year — most recent first.\n" +
	            "No userId needed — resolved from JWT.")
	    
	    	public ResponseEntity<List<EmployeeEducationResponseDTO>> getMyEducation( @AuthenticationPrincipal UserDetails userDetails) {
	 
	        Long userId = getUserId(userDetails.getUsername());
	        logger.info("Get education for: {}",  userDetails.getUsername());
	        return ResponseEntity.ok( emEducationService.getMyEducation(userId));
	    }
	 

	    @PutMapping("/finsecure/employee/education/{eduId}")
	    @PreAuthorize("hasAuthority('EMPLOYEE')")
	    @Operation(summary = "Update education record (Employee)",description =
	            "Employee updates one of their education records.\n" +
	            "Get eduId from GET /finsecure/employee/education.\n" +
	            "All fields optional — only provided fields updated.\n" +
	            "Employee can only update own records."
	    )
	    public ResponseEntity<EmployeeEducationResponseDTO> updateEducation( @PathVariable Integer eduId, 
	    		@Valid @RequestBody EmployeeEducationRequestDTO dto, @AuthenticationPrincipal UserDetails userDetails) {
	 
	        Long userId = getUserId(userDetails.getUsername());
	        logger.info("Update eduId: {} for: {}", eduId, userDetails.getUsername());
	        return ResponseEntity.ok( emEducationService.updateEducation( eduId, dto, userId));
	    }
	 

	    @DeleteMapping("/finsecure/employee/education/{eduId}")
	    @PreAuthorize("hasAuthority('EMPLOYEE')")
	    @Operation( summary = "Delete education record (Employee)", description =
	            "Permanently deletes one education record.\n" +
	            "Get eduId from GET /finsecure/employee/education.\n" +
	            "Employee can only delete own records.")
	    
	    	public ResponseEntity<String> deleteEducation( @PathVariable Integer eduId, @AuthenticationPrincipal UserDetails userDetails) {
	 
	        Long userId = getUserId(userDetails.getUsername());
	        logger.warn("Delete eduId: {} for: {}", eduId, userDetails.getUsername());
	        emEducationService.deleteEducation(eduId, userId);
	        return ResponseEntity.ok( "Education record deleted: " + eduId);
	    }
	 
	
	    @GetMapping("/finsecure/hr/employee/{userId}/education")
	    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
	    @Operation(summary = "Get employee education records (HR / Admin)", description =
	            "HR views any employee education history.\n" +
	            "Find userId via " +
	            "GET /finsecure/hr/employees/search first.\n" +
	            "Sorted by passing year — most recent first.")
	    
	    	public ResponseEntity<List<EmployeeEducationResponseDTO>> getEducationByUserId( @PathVariable Long userId) {
	 
	        logger.info("HR get education for userId: {}", userId);
	        return ResponseEntity.ok(emEducationService.getEducationByUserId(userId));
	    }
    
}//end class