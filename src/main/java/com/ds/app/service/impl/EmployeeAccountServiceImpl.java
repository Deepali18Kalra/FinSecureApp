package com.ds.app.service.impl;
import com.ds.app.dto.request.BankValidationReviewRequestDTO;
import com.ds.app.dto.request.EmployeeBankAccountRequestDTO;
import com.ds.app.dto.response.EmployeeBankAccountResponseDTO;
import com.ds.app.entity.FinanceBankAccount;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeBankAccount;
import com.ds.app.enums.BankStatus;
import com.ds.app.enums.BankValidationStatus;
import com.ds.app.exception.BlacklistedBankException;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.FinanceBankRepository;
import com.ds.app.repository.EmployeeBankAccountRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.EmployeeAccountService;

import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
@Service
public class EmployeeAccountServiceImpl implements EmployeeAccountService {
	
	@Autowired
	private EmployeeBankAccountRepository employeeBankAccountRepository;
	
	@Autowired
	private FinanceBankRepository allBankRepository;
	
	@Autowired 
	private EmployeeRepository employeeRepository;

	@Override
	@Transactional
	public EmployeeBankAccountResponseDTO addBankAccount(EmployeeBankAccountRequestDTO dto, Long employeeId) throws ResourceNotFoundException, BlacklistedBankException {
		
		Employee employee = employeeRepository.findByUserId(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with user ID: " + employeeId));
		
		
		
		if(employeeBankAccountRepository.existsByEmployee_UserId(employeeId)) {
			throw new ResourceNotFoundException("Employee already has a bank account registered.");
		}
		
		FinanceBankAccount bank = allBankRepository.findById(dto.getBankId())
				.orElseThrow(() -> new ResourceNotFoundException("Bank not found with ID: " + dto.getBankId()));
		
		
		if(bank.getStatus()==BankStatus.BLACKLISTED)
		{
			throw new BlacklistedBankException(
					"ALERT : Bank " + bank.getBankName() + " ( " + bank.getBankCode() +" ) is blacklisted and cannot be used for employee bank accounts."   
					);
		}
		
		
		EmployeeBankAccount account = EmployeeBankAccount.builder()
				.employee(employee)
				.accountHolderName(employee.getFirstName() +" "+employee.getLastName())
				.bank(bank)
				.accountNumber(dto.getAccountNumber())
				.ifscCode(dto.getIfscCode().toUpperCase())
				.validationStatus(BankValidationStatus.PENDING)
				.build();
		
		return mapToResponseDTO(employeeBankAccountRepository.save(account));
	}

	@Override
	@Transactional
	public EmployeeBankAccountResponseDTO updateBankAccount(EmployeeBankAccountRequestDTO dto, Long employeeId) throws BlacklistedBankException, ResourceNotFoundException {
		
		EmployeeBankAccount account = employeeBankAccountRepository.findByEmployee_UserId(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("No Bank account not found for employee ID: " + employeeId));
		
		FinanceBankAccount bank = allBankRepository.findById(dto.getBankId())
				.orElseThrow(() -> new ResourceNotFoundException("Bank not found with ID: " + dto.getBankId()));
		
		if(bank.getStatus()==BankStatus.BLACKLISTED)
		{
			throw new BlacklistedBankException(
					"ALERT : Bank " + bank.getBankName() + " ( " + bank.getBankCode() +" ) is blacklisted and cannot be used for employee bank accounts."   
					);
		}
		
		account.setBank(bank);
		account.setAccountNumber(dto.getAccountNumber());
		account.setIfscCode(dto.getIfscCode().toUpperCase());
		account.setAccountHolderName(dto.getAccountHolderName());
		account.setValidationStatus(BankValidationStatus.PENDING); // reset to pending on update
		
		return mapToResponseDTO(employeeBankAccountRepository.save(account));
		
				
		
		
	}

	@Override
	public EmployeeBankAccountResponseDTO getMyBankAccount(Long employeeId) throws ResourceNotFoundException {
		EmployeeBankAccount account = employeeBankAccountRepository.findByEmployee_UserId(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("No Bank account not found for employee ID: " + employeeId));
		
		
		return mapToResponseDTO(account);
	}

	@Override
	@Transactional
	public EmployeeBankAccountResponseDTO reviewBankAccount(Long accountId, BankValidationReviewRequestDTO dto,
			Long reviewedBy) throws ResourceNotFoundException {
		
		EmployeeBankAccount account = employeeBankAccountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Bank account not found with ID: " + accountId));
		
		if(dto.getValidationStatus()==BankValidationStatus.PENDING)
		{
			throw new IllegalArgumentException("Validation status cannot be set back to PENDING during review.");
		}
		
		account.setValidationStatus(dto.getValidationStatus());
		account.setReviewedBy(reviewedBy);
		
		if(dto.getComments() != null && !dto.getComments().isBlank()) {
			account.setReviewNote(dto.getComments());
		}
		
		return mapToResponseDTO(employeeBankAccountRepository.save(account));

	}

	@Override
	public Page<EmployeeBankAccountResponseDTO> getAllAccounts(
	        int page, int size, BankValidationStatus status, Long bankId, String holderName) {

	    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

	    Page<EmployeeBankAccount> accountPage;

	    if (holderName != null && !holderName.isBlank()) {
            return employeeBankAccountRepository
                    .searchByAccountHolderName(holderName, pageable)
                    .map(this::mapToResponseDTO);
        }

        if (bankId != null) {
            return employeeBankAccountRepository
                    .findByBank_BankId(bankId, pageable)
                    .map(this::mapToResponseDTO);
        }

        if (status != null) {
            return employeeBankAccountRepository
                    .findByValidationStatus(status, pageable)
                    .map(this::mapToResponseDTO);
        }
        return employeeBankAccountRepository.findAll(pageable).map(this::mapToResponseDTO);
	}


	@Override
	public EmployeeBankAccountResponseDTO getAccountById(Long accountId) throws ResourceNotFoundException {
		 EmployeeBankAccount account = employeeBankAccountRepository.findById(accountId)
	                .orElseThrow(() -> new ResourceNotFoundException(
	                        "Bank account not found with ID: " + accountId));
	        return mapToResponseDTO(account);
	}

	@Override
	public Map<String, Long> getBankAccountStatusSummary() {
		 Map<String, Long> summary = new HashMap<>();
	        summary.put("TOTAL"   ,    employeeBankAccountRepository.count());
	        summary.put("PENDING" ,  employeeBankAccountRepository.countByValidationStatus(BankValidationStatus.PENDING));
	        summary.put("APPROVED", employeeBankAccountRepository.countByValidationStatus(BankValidationStatus.APPROVED));
	        summary.put("REJECTED", employeeBankAccountRepository.countByValidationStatus(BankValidationStatus.REJECTED));
	        return summary;
	}
	
	
	private EmployeeBankAccountResponseDTO mapToResponseDTO(EmployeeBankAccount account) {
		EmployeeBankAccountResponseDTO dto = new EmployeeBankAccountResponseDTO();
		
//		dto.setId(account.getId());
		dto.setEmpBankId(account.getEmpBankId());
		
		if(account.getEmployee() != null) {
			dto.setEmployeeId(account.getEmployee().getUserId());
//			dto.setAccountHolderName(account.getEmployee().getUsername());
		}
		
		if(account.getBank() != null) {
			dto.setBankName(account.getBank().getBankName());
//			dto.setId(account.getBank().getBankId
//			dto.setBankId(account.getBank().getBankId());
//			dto.setEmpBankId(account.getBank().getBankId());
			dto.setBankCode(account.getBank().getBankCode());
		}
		
		dto.setIfscCode(account.getIfscCode());
		
		dto.setAccountNumberMasked(maskedAccountNumber(account.getAccountNumber()));
		dto.setAccountHolderName(account.getAccountHolderName());
		dto.setValidationStatus(account.getValidationStatus());
		dto.setReviewedBy(account.getReviewedBy());
		dto.setReviewNote(account.getReviewNote());
		dto.setCreatedAt(account.getCreatedAt());
		
		return dto;
	}
	
	private String maskedAccountNumber(String accountNumber) {
		if(accountNumber == null || accountNumber.length() < 4) 
		{
			return "****";
		}
		String last4Digits = accountNumber.substring(accountNumber.length() - 4);
		return "****" + last4Digits;
	}

	@Override
	public Page<Employee> findByBankAccountIsNull(int page, int size) {
		
		Pageable pageale = PageRequest.of(page, size);
		Page<Employee> accounts= employeeRepository.findByBankAccountIsNull(pageale);
		return accounts;	
	}
	
}
