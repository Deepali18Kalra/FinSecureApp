package com.ds.app.service;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.ds.app.dto.response.EmployeeInvestmentResponseDTO;
import com.ds.app.dto.response.SalaryJobResponseDTO;
import com.ds.app.dto.response.SalaryRecordResponseDTO;

public interface ReportService {
	
	Page<SalaryRecordResponseDTO>getSalaryRegister(String month , int page,  int size);
	
	Page<EmployeeInvestmentResponseDTO>getComplianceReport(int page , int size);
	
	Map<String, Long>getBankAccountStatusReport();
	
	Page<SalaryJobResponseDTO>getSalaryJobReport(int page , int size);
	
	
	
	
}
