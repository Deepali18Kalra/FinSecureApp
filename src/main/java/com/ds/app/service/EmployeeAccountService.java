package com.ds.app.service;


import java.util.Map;

import org.springframework.data.domain.Page;

import com.ds.app.dto.request.BankValidationReviewRequestDTO;
import com.ds.app.dto.request.EmployeeBankAccountRequestDTO;
import com.ds.app.dto.response.EmployeeBankAccountResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeBankAccount;
import com.ds.app.enums.BankValidationStatus;
import com.ds.app.exception.BankAccountLockedException;
import com.ds.app.exception.BlacklistedBankException;
import com.ds.app.exception.ResourceNotFoundException;

public interface EmployeeAccountService {
	
	EmployeeBankAccountResponseDTO addBankAccount(EmployeeBankAccountRequestDTO dto, Long employeeId) throws ResourceNotFoundException, BlacklistedBankException;
	
	EmployeeBankAccountResponseDTO updateBankAccount(EmployeeBankAccountRequestDTO dto, Long employeeId) throws  BankAccountLockedException, ResourceNotFoundException, BlacklistedBankException;
	
	EmployeeBankAccountResponseDTO getMyBankAccount(Long employeeId) throws ResourceNotFoundException;
	
	void resetToZero(Long id) throws ResourceNotFoundException;
	
	EmployeeBankAccount getWholeBankAccount(Long id)throws ResourceNotFoundException;
	
	void unlockAccount(Long id)throws ResourceNotFoundException;
	
	
	EmployeeBankAccountResponseDTO reviewBankAccount(Long accountId, BankValidationReviewRequestDTO dto, Long reviewedBy) throws ResourceNotFoundException;
	
	Page<EmployeeBankAccountResponseDTO>getAllAccounts(int page , int size , BankValidationStatus status , Long bankId , String accountHolderName) throws ResourceNotFoundException;
	
	EmployeeBankAccountResponseDTO getAccountById(Long accountId) throws ResourceNotFoundException;
	
	Map<String , Long>getBankAccountStatusSummary();
	
	Page<Employee>findByBankAccountIsNull(int page , int size);
}
