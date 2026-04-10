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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.app.dto.request.*;
import com.ds.app.dto.response.*;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.EmployeeCRUDService;
import com.ds.app.service.EmployeePhotoService;
import com.ds.app.service.EmployeeRewardService;
import com.ds.app.service.impl.EmployeePhotoServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
public class HrProfileController {
	
	@Autowired
	EmployeeCRUDService employeeCRUDService;
	
	@Autowired
	EmployeeRewardService employeeRewardService;
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeePhotoServiceImpl.class);

	@PostMapping("/finsecure/hr/employee/create")
    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
    @Operation(summary = "Create employee account (STEP 2)",
        description = "HR creates the employee account. System auto-generates employeeCode. " +
                      "Response includes credentials for HR to forward to the employee. " +
                      "Personal info (name, email, phone) is NOT set here — employee fills it in Step 4."
    )
    public ResponseEntity<HRCreateEmployeeResponseDTO> createEmployee(
            @Valid @RequestBody HRCreateEmployeeRequestDTO dto,@AuthenticationPrincipal MyUserDetails userDetails) throws Exception {
       
		logger.info("HR '{}' creating employee: {}", userDetails.getUsername(), dto.getUsername());
        return new ResponseEntity<>(employeeCRUDService.createEmployeeByHR(dto, userDetails.getUsername()), HttpStatus.CREATED);
    }
	
	
	
	@GetMapping("/finsecure/hr/employee/{userId}")
    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
    @Operation(summary = "Get employee by userId (HR / Admin)",
	        description = 
	        "Fetch any employee's full profile. " +  
	        "Use GET /finsecure/hr/employees/search first to find the userId."
    )
    public ResponseEntity<EmployeeProfileResponseDTO> getEmployeeById(@PathVariable Long userId) throws Exception {
		
        logger.info("HR fetching employee for userId: {}", userId);
        return ResponseEntity.ok(employeeCRUDService.getEmployeeById(userId));
    }
	
	
	 @PutMapping("/finsecure/hr/update/employee/{userId}")
	    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
	    @Operation(
	        summary = "Update professional fields (HR / Admin)",
	        description = "Updates: department, designation, employmentType, certificationStatus, " +
	                      "certificationName, certificationExpiryDate, employeeExperience. " +
	                      "If certificationStatus = CERTIFIED, name and expiry date are required."
	    )
	    public ResponseEntity<EmployeeProfileResponseDTO> updateEmployeeByHr(@PathVariable Long userId,@Valid @RequestBody EmployeeHRUpdateDTO dto,  
	            @AuthenticationPrincipal MyUserDetails userDetails) throws Exception {
	       
		 logger.info("HR '{}' updating userId: {}", userDetails.getUsername(), userId);
	        return ResponseEntity.ok(employeeCRUDService.updateEmployeeByHr(userId, dto, userDetails.getUsername()));
	    }
	 
}
	
