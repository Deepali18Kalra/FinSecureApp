package com.ds.app.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ds.app.dto.request.BankValidationReviewRequestDTO;
import com.ds.app.dto.request.EmployeeBankAccountRequestDTO;
import com.ds.app.dto.response.EmployeeBankAccountResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.enums.BankValidationStatus;
import com.ds.app.exception.BankAccountLockedException;
import com.ds.app.exception.BlacklistedBankException;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.service.EmployeeAccountService;

import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/finsecure/finance/bank-accounts")
public class EmployeeBankAccountController {

	@Autowired
	private EmployeeAccountService employeeAccountService;

	private Long getLoggedInUserId() {
		MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return userDetails.getUser().getUserId();
	}

	@PostMapping("/bank-account")
	@PreAuthorize("hasAnyAuthority('EMPLOYEE','FINANCE','HR','ADMIN')")	public ResponseEntity<EmployeeBankAccountResponseDTO> registerBankAccount(
			@RequestBody EmployeeBankAccountRequestDTO dto) throws ResourceNotFoundException, BlacklistedBankException {

		EmployeeBankAccountResponseDTO response = employeeAccountService.addBankAccount(dto, getLoggedInUserId());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/employee/bank-account")
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	public ResponseEntity<EmployeeBankAccountResponseDTO> updateBankAccount(
			@Valid @RequestBody EmployeeBankAccountRequestDTO dto)
			throws ResourceNotFoundException, BlacklistedBankException, BankAccountLockedException {

		EmployeeBankAccountResponseDTO response = employeeAccountService.updateBankAccount(dto, getLoggedInUserId());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/employee/bank-account")
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	public ResponseEntity<EmployeeBankAccountResponseDTO> getMyBankAccount() throws ResourceNotFoundException {

		EmployeeBankAccountResponseDTO response = employeeAccountService.getMyBankAccount(getLoggedInUserId());
		return ResponseEntity.ok(response);
	}

	@GetMapping
	@PreAuthorize("hasAuthority('FINANCE')")
	public ResponseEntity<Page<EmployeeBankAccountResponseDTO>> getAllBankAccounts(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) BankValidationStatus status, @RequestParam(required = false) Long bankId,
			@RequestParam(required = false) String holderName) throws ResourceNotFoundException {

		Page<EmployeeBankAccountResponseDTO> results = employeeAccountService.getAllAccounts(page, size, status, bankId,
				holderName);
		return ResponseEntity.ok(results);

	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('EMPLOYEE,FINANCE')")
	public ResponseEntity<EmployeeBankAccountResponseDTO> getBankAccountById(@PathVariable Long id)
			throws ResourceNotFoundException {

		return ResponseEntity.ok(employeeAccountService.getAccountById(id));
	}

	@GetMapping("/unregistered")
	public ResponseEntity<Page<Employee>> getUnregisteredEmployees() {
		Page<Employee> accounts = employeeAccountService.findByBankAccountIsNull(0, 10);

		return ResponseEntity.ok(accounts);
	}

	@PutMapping("/{id}/review")
	@PreAuthorize("hasAuthority('FINANCE')")
	public ResponseEntity<EmployeeBankAccountResponseDTO> reviewBankAccount(@PathVariable Long id,
			@Valid @RequestBody BankValidationReviewRequestDTO dto) throws ResourceNotFoundException {

		EmployeeBankAccountResponseDTO response = employeeAccountService.reviewBankAccount(id, dto,
				getLoggedInUserId());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/summary")
	@PreAuthorize("hasAuthority('FINANCE')")
	public ResponseEntity<Map<String, Long>> getBankAccountSummary() {
		return ResponseEntity.ok(employeeAccountService.getBankAccountStatusSummary());
	}
}
