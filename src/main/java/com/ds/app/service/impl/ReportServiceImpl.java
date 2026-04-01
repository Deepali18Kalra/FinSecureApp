package com.ds.app.service.impl;

import com.ds.app.dto.response.EmployeeInvestmentResponseDTO;
import com.ds.app.dto.response.SalaryJobResponseDTO;
import com.ds.app.dto.response.SalaryRecordResponseDTO;
import com.ds.app.entity.EmployeeInvestment;
import com.ds.app.entity.SalaryRecord;
import com.ds.app.enums.BankValidationStatus;
import com.ds.app.enums.ComplianceStatus;
import com.ds.app.repository.EmployeeBankAccountRepository;
import com.ds.app.repository.EmployeeInvestmentRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.SalaryJobRepository;
import com.ds.app.repository.SalaryRecordRepository;
import com.ds.app.service.ReportService;

import lombok.RequiredArgsConstructor;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

	private final SalaryRecordRepository salaryRecordRepository;
	private final EmployeeInvestmentRepository investmentRepository;
	private final EmployeeBankAccountRepository bankAccountRepository;
	private final SalaryJobRepository salaryJobRepository;
	private final EmployeeRepository employeeRepository;

	@Override
	public Page<SalaryRecordResponseDTO> getSalaryRegister(String month, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("creditedAt").descending());

		if (month != null && !month.isBlank()) {
			YearMonth targetMonth = YearMonth.parse(month);
			return salaryRecordRepository.findBySalaryMonth(targetMonth, pageable).map(record -> mapRecordToResponse(record));
		}

		return salaryRecordRepository.findAll(pageable).map(this::mapRecordToResponse);
	}

	@Override
	public Page<EmployeeInvestmentResponseDTO> getComplianceReport(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("declaredAt").descending());
		return investmentRepository.findByComplianceStatus(ComplianceStatus.NON_COMPLIANT, pageable)
				.map(this::mapInvestmentToResponse);
	}

	@Override
	public Map<String, Long> getBankAccountStatusReport() {
		Map<String, Long> report = new LinkedHashMap<>();

		report.put("total", bankAccountRepository.count());
		report.put("pending", bankAccountRepository.countByValidationStatus(BankValidationStatus.PENDING));
		report.put("approved", bankAccountRepository.countByValidationStatus(BankValidationStatus.APPROVED));
		report.put("rejected", bankAccountRepository.countByValidationStatus(BankValidationStatus.REJECTED));
		return report;
	}

	@Override
	public Page<SalaryJobResponseDTO> getSalaryJobReport(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
		return salaryJobRepository.findAll(pageable).map(this::mapJobToResponse);
	}

	

	private String maskAccount(String accountNumber) {
		if (accountNumber == null || accountNumber.length() < 4)
			return "****";
		return "****" + accountNumber.substring(accountNumber.length() - 4);
	}

	private SalaryRecordResponseDTO mapRecordToResponse(SalaryRecord record) {
		SalaryRecordResponseDTO dto = new SalaryRecordResponseDTO();
		dto.setId(record.getId());
		dto.setEmployeeId(record.getEmployee().getUserId());
		dto.setEmployeeName(record.getEmployee().getFirstName() + " " + record.getEmployee().getLastName());
		dto.setSalaryMonth(record.getSalaryMonth().toString());
		dto.setGrossSalary(record.getGrossSalary());
		dto.setDeductions(record.getDeductions());
		dto.setNetSalary(record.getNetSalary());
		dto.setPaymentStatus(record.getPaymentStatus());
		dto.setCreditedAt(record.getCreditedAt());
		if (record.getBankAccount() != null) {
			dto.setBankAccountMasked(maskAccount(record.getBankAccount().getAccountNumber()));
			dto.setBankName(record.getBankAccount().getBank().getBankName());
		}
		return dto;
	}
	 private EmployeeInvestmentResponseDTO mapInvestmentToResponse(EmployeeInvestment inv) {
	        EmployeeInvestmentResponseDTO dto = new EmployeeInvestmentResponseDTO();
	        dto.setEmpInvestmentId(inv.getEmpInvestmentId());
	        dto.setEmployeeId(inv.getEmployee().getUserId());
	        dto.setEmployeeName(inv.getEmployee().getFirstName()
	                + " " + inv.getEmployee().getLastName());
	        dto.setInvestmentType(inv.getInvestmentType());
	        dto.setDeclaredAmount(inv.getDeclaredAmount());
	        dto.setComplianceStatus(inv.getComplianceStatus());
	        dto.setReviewNote(inv.getReviewNote());
	        dto.setDeclaredAt(inv.getDeclaredAt());
	        if (inv.getMutualFund() != null) {
	            dto.setFundName(inv.getMutualFund().getFundName());
	            dto.setFundCode(inv.getMutualFund().getFundCode());
	        }
	        if (inv.getSecurityName() != null) {
	            dto.setSecurityName(inv.getSecurityName());
	        }
	        return dto;
	    }

	    private SalaryJobResponseDTO mapJobToResponse(
	            com.ds.app.entity.SalaryJob job) {
	        SalaryJobResponseDTO dto = new SalaryJobResponseDTO();
	        dto.setId(job.getId());
	        dto.setJobName(job.getJobName());
	        dto.setScheduledDateTime(job.getScheduledDateTime());
	        dto.setTargetMonth(job.getTargetMonth().toString());
	        dto.setJobStatus(job.getJobStatus());
	        dto.setTotalEmployees(job.getTotalEmployees());
	        dto.setSuccessCount(job.getSuccessCount());
	        dto.setFailureCount(job.getFailureCount());
	        dto.setCreatedBy(job.getCreatedBy());
	        return dto;
	    }
}
