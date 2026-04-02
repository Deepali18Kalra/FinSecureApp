package com.ds.app.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ds.app.dto.request.EmployeeInvestmentRequestDTO;
import com.ds.app.dto.request.InvestmentReviewRequestDTO;
import com.ds.app.dto.response.EmployeeInvestmentResponseDTO;
import com.ds.app.enums.ComplianceStatus;
import com.ds.app.exception.InvestmentComplianceException;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.service.EmployeeInvestmentService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/finsecure/finance/investments")
public class EmployeeInvestmentController {

	@Autowired
	EmployeeInvestmentService employeeInvestmentService;

	@Autowired
	iAppUserRepository appUserRepository;

	private Long getLoggedInUserId() throws ResourceNotFoundException {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			String username = ((UserDetails) principal).getUsername();
			return appUserRepository.findByUsername(username)
					.orElseThrow(() -> new ResourceNotFoundException("User not found")).getUserId();
		}
		throw new IllegalStateException("Unable to resolve authenticated user");
	}

	// Employee declares investment
	@PostMapping("/{employeeId}")
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	public EmployeeInvestmentResponseDTO declareInvestment(@PathVariable Long employeeId,
			@Valid @RequestBody EmployeeInvestmentRequestDTO dto) throws ResourceNotFoundException, InvestmentComplianceException {
		return employeeInvestmentService.declareInvestment(employeeId, dto);
	}
	
	// Finance reviews investment
	@PutMapping("/{id}/review")
	@PreAuthorize("hasAuthority('FINANCE')")
	public EmployeeInvestmentResponseDTO reviewInvestment(
	@PathVariable Long id,
	@Valid @RequestBody InvestmentReviewRequestDTO dto) throws ResourceNotFoundException, InvestmentComplianceException {
	return employeeInvestmentService.reviewInvestment(id, dto, getLoggedInUserId());
	}

	// Get investment by id
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('EMPLOYEE,FINANCE')")
	public EmployeeInvestmentResponseDTO getById(@PathVariable Long id) throws ResourceNotFoundException {
	return employeeInvestmentService.getInvestmentById(id);
	}

	// Get all investments of one employee
	@GetMapping("/employee/{employeeId}")
	@PreAuthorize("hasAuthority('EMPLOYEE,FINANCE')")
	public Page<EmployeeInvestmentResponseDTO> getByEmployee(
	@PathVariable Long employeeId,
	@RequestParam(defaultValue = "0") int page,
	@RequestParam(defaultValue = "10") int size) throws ResourceNotFoundException {
	return employeeInvestmentService.getInvestmentsByEmployee(employeeId, page, size);
	}

	// Get all investments — Finance/Admin/HR
	@GetMapping
	@PreAuthorize("hasAuthority('EMPLOYEE,FINANCE')")
	public Page<EmployeeInvestmentResponseDTO> getAll(
	@RequestParam(defaultValue = "0") int page,
	@RequestParam(defaultValue = "10") int size,
	@RequestParam(required = false) ComplianceStatus status) throws ResourceNotFoundException {

	if (status != null) {
	return employeeInvestmentService.getByComplianceStatus(status, page, size);
	}
	return employeeInvestmentService.getAllInvestments(getLoggedInUserId(),page, size);
	}

	
}
