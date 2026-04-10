package com.ds.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.app.dto.response.EmployeeProfileResponseDTO;
import com.ds.app.dto.response.PagedResponseDTO;
import com.ds.app.exception.EmployeeNotFoundException1;
import com.ds.app.service.EmployeeAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@Tag(name = "Employee Profile Admin",description = "HR/Admin — soft delete, restore " +"and view deleted employee profiles")
public class EmployeeProfileAdminController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeProfileAdminController.class);

    @Autowired
    private EmployeeAdminService employeeAdminService;

	   
	    @DeleteMapping("/finsecure/admin/employee/{userId}")
	    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
	    @Operation(
	        summary = "Soft delete employee (HR / Admin)",
	        description =
	            "Sets isDeleted = true.\n\n" +
	            "Record retained in DB — " +
	            "excluded from normal queries.\n\n" +
	            "Idempotent — safe to call twice.\n\n" +
	            "Find userId via " +
	            "GET /finsecure/hr/employees/search first."
	    )
	    public ResponseEntity<String> softDeleteEmployee( @PathVariable Long userId) throws Exception {
	
	        logger.warn("Soft delete requested for userId: {}",userId);
	
	        employeeAdminService.softDeleteEmployee(userId);
	
	        return ResponseEntity.ok("Employee soft deleted successfully " + "for userId: " + userId);
	    }
	
	
	    @PutMapping("/finsecure/admin/employee/{userId}/restore")
	    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
	    @Operation(
	        summary = "Restore deleted employee (HR / Admin)",
	        description =
	            "Sets isDeleted = false.\n\n" +
	            "Employee can login and " +
	            "appear in searches again.\n\n" +
	            "Find userId via " +
	            "GET /finsecure/admin/employees/deleted first."
	    )
	    public ResponseEntity<String> restoreEmployee( @PathVariable Long userId) throws EmployeeNotFoundException1 {
	
	        logger.info("Restore requested for userId: {}",userId);
	
	        employeeAdminService.restoreEmployee(userId);
	
	        return ResponseEntity.ok("Employee restored successfully " + "for userId: " + userId);
	    }
	
	   
	    @GetMapping("/finsecure/admin/employees/deleted")
	    @PreAuthorize("hasAnyAuthority('HR', 'ADMIN')")
	    @Operation(
	        summary = "View deleted employees (HR / Admin)",
	        description =
	            "Returns paginated list of " +
	            "all soft deleted employees.\n\n" +
	            "Use returned userId to call restore endpoint."
	    )
	    public ResponseEntity<PagedResponseDTO<EmployeeProfileResponseDTO>> findDeletedEmployees(@ParameterObject
	            @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.DESC) Pageable pageable) {
	
	        logger.info("Finding all deleted employees");
	
	        return ResponseEntity.ok(employeeAdminService.findDeletedEmployees(pageable));
	    }
	}