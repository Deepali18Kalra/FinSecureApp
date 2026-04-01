package com.ds.app.service;

import org.springframework.data.domain.Page;

import com.ds.app.dto.request.EmployeeInvestmentRequestDTO;
import com.ds.app.dto.request.InvestmentReviewRequestDTO;
import com.ds.app.dto.response.EmployeeInvestmentResponseDTO;
import com.ds.app.enums.ComplianceStatus;
import com.ds.app.exception.InvestmentComplianceException;
import com.ds.app.exception.ResourceNotFoundException;

public interface EmployeeInvestmentService {
	
	EmployeeInvestmentResponseDTO declareInvestment(Long empid , EmployeeInvestmentRequestDTO dto) throws ResourceNotFoundException , InvestmentComplianceException;
	
	EmployeeInvestmentResponseDTO reviewInvestment(Long mutualF , InvestmentReviewRequestDTO dto , Long reviewedBy) throws ResourceNotFoundException, InvestmentComplianceException;
	
	EmployeeInvestmentResponseDTO getInvestmentById(Long id) throws ResourceNotFoundException;
	
	Page<EmployeeInvestmentResponseDTO> getAllInvestments(Long employeeId,int page , int size) throws ResourceNotFoundException;
	
	Page<EmployeeInvestmentResponseDTO> getByComplianceStatus(ComplianceStatus status , int page , int size) throws ResourceNotFoundException;
	
	Page<EmployeeInvestmentResponseDTO>getInvestmentsByEmployee(Long employeeId,int page,int size) throws ResourceNotFoundException;
	
}
