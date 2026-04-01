package com.ds.app.service.impl;

import com.ds.app.dto.request.EmployeeInvestmentRequestDTO;
import com.ds.app.dto.request.InvestmentReviewRequestDTO;
import com.ds.app.dto.response.EmployeeInvestmentResponseDTO;
import com.ds.app.entity.FinanceInvestment;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeInvestment;
import com.ds.app.enums.ComplianceStatus;
import com.ds.app.enums.FundStatus;
import com.ds.app.enums.InvestmentType;
import com.ds.app.exception.InvestmentComplianceException;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.FinanceInvestmentRepository;
import com.ds.app.repository.EmployeeInvestmentRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.service.EmployeeInvestmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class EmployeeInvestmentServiceImpl implements EmployeeInvestmentService {

	@Autowired
	EmployeeInvestmentRepository employeeInvestmentRepository;

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	iAppUserRepository appUserRepository;

	@Autowired
	FinanceInvestmentRepository companyMutualFundRepository;

	@Override
	public EmployeeInvestmentResponseDTO declareInvestment(Long empid, EmployeeInvestmentRequestDTO dto) throws InvestmentComplianceException, ResourceNotFoundException {

// fetch employee
		Employee employee = employeeRepository.findById(empid)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + empid));

// step 2 — validate request based on investment type
		validateInvestmentRequest(dto);

// step 3 — resolve mutual fund if type is MUTUAL_FUND
		FinanceInvestment mutualFund = null;
		if (dto.getInvestmentType() == InvestmentType.MUTUAL_FUND) {
			mutualFund = companyMutualFundRepository.findById(dto.getFundId()).orElseThrow(
					() -> new ResourceNotFoundException("Mutual fund not found with id: " + dto.getFundId()));
		}

// step 4 — check for duplicate declaration
		if (dto.getInvestmentType() == InvestmentType.MUTUAL_FUND) {
			if (employeeInvestmentRepository.existsByEmployee_UserIdAndMutualFund_MutualFundId(empid,
					dto.getFundId())) {
				throw new InvestmentComplianceException("Employee has already declared investment in this fund");
			}
		} else if (dto.getSecurityName() != null) {
			if (employeeInvestmentRepository.existsByEmployee_UserIdAndSecurityName(empid, dto.getSecurityName())) {
				throw new InvestmentComplianceException(
						"Employee has already declared investment in " + dto.getSecurityName());
			}
		}

// step 5 — run compliance check
		ComplianceStatus complianceStatus = checkCompliance(employee, dto, mutualFund);

// step 6 — build and save entity
		EmployeeInvestment investment = EmployeeInvestment.builder().employee(employee)
				.investmentType(dto.getInvestmentType()).mutualFund(mutualFund).securityName(dto.getSecurityName())
				.declaredAmount(dto.getDeclaredAmount()).complianceStatus(complianceStatus).build();

		return mapToResponse(employeeInvestmentRepository.save(investment));
	}

	private ComplianceStatus checkCompliance(Employee employee, EmployeeInvestmentRequestDTO dto,
			FinanceInvestment mutualFund) {

// if company has no investment restriction → always compliant
		if (employee.getCompanyId() == null) {
			return ComplianceStatus.NON_COMPLIANT;
		}

// HR module not integrated yet send to PENDING_REVIEW
// once HR module is ready, fetch company and check investmentRestricted flag
// for now assume all employees with companyId have restrictions
// TODO: replace this with actual company fetch after HR module integration
		boolean isRestricted = true;

		if (!isRestricted) {
			return ComplianceStatus.NON_COMPLIANT;

		}
// company restricts investments
// DIRECT_EQUITY or BONDS → always NON_COMPLIANT for restricted companies
		if (dto.getInvestmentType() == InvestmentType.DIRECT_EQUITY
				|| dto.getInvestmentType() == InvestmentType.BONDS) {
			return ComplianceStatus.NON_COMPLIANT;
		}

// MUTUAL_FUND → check if fund is in whitelist
		if (dto.getInvestmentType() == InvestmentType.MUTUAL_FUND) {
			if (mutualFund == null) {
				return ComplianceStatus.NON_COMPLIANT;
			}
			if (mutualFund.getStatus() == FundStatus.WHITELISTED) {
				return ComplianceStatus.NON_COMPLIANT;
			} else {
				return ComplianceStatus.NON_COMPLIANT;
			}
		}
		return ComplianceStatus.PENDING_REVIEW;

	}

	private void validateInvestmentRequest(EmployeeInvestmentRequestDTO dto) throws InvestmentComplianceException {
// TODO Auto-generated method stub
		if (dto.getInvestmentType() == InvestmentType.MUTUAL_FUND && dto.getFundId() == null) {
			throw new InvestmentComplianceException("Fund ID is required when investment type is MUTUAL_FUND");
		}

		if ((dto.getInvestmentType() == InvestmentType.DIRECT_EQUITY || dto.getInvestmentType() == InvestmentType.BONDS)
				&& (dto.getSecurityName() == null || dto.getSecurityName().isBlank())) {
			throw new InvestmentComplianceException("Security name is required for DIRECT_EQUITY or BONDS");
		}

	}

	@Override
	public EmployeeInvestmentResponseDTO reviewInvestment(
	        Long investmentId,
	        InvestmentReviewRequestDTO dto,
	        Long reviewedBy
	) throws ResourceNotFoundException, InvestmentComplianceException {

	    EmployeeInvestment investment =
	        employeeInvestmentRepository.findById(investmentId)
	            .orElseThrow(() ->
	                new ResourceNotFoundException("Investment not found with id: " + investmentId)
	            );

	    // ✅ ONLY block if already COMPLIANT
	    if (investment.getComplianceStatus() == ComplianceStatus.COMPLIANT) {
	        throw new InvestmentComplianceException(
	            "Investment is already compliant"
	        );
	    }

	    investment.setComplianceStatus(dto.getComplianceStatus());
	    investment.setReviewNote(dto.getReviewNote());
	    investment.setReviewedBy(reviewedBy);

	    return mapToResponse(employeeInvestmentRepository.save(investment));
	}
	@Override
	public EmployeeInvestmentResponseDTO getInvestmentById(Long id) throws ResourceNotFoundException {
		EmployeeInvestment investment = employeeInvestmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Investment not found with id: " + id));
		return mapToResponse(investment);
	}
	@Override
	public Page<EmployeeInvestmentResponseDTO> getAllInvestments(Long employeeId, int page, int size) throws ResourceNotFoundException {
// check employee exists
		employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

		Pageable pageable = PageRequest.of(page, size, Sort.by("declaredAt").descending());
		return employeeInvestmentRepository.findByEmployee_UserId(employeeId, pageable).map(this::mapToResponse);
	}

	@Override
	public Page<EmployeeInvestmentResponseDTO> getByComplianceStatus(ComplianceStatus status, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("declaredAt").descending());
		return employeeInvestmentRepository.findByComplianceStatus(status, pageable).map(this::mapToResponse);
	}

	private EmployeeInvestmentResponseDTO mapToResponse(EmployeeInvestment investment) {

		EmployeeInvestmentResponseDTO dto = new EmployeeInvestmentResponseDTO();

// dto.setId(investment.getEmpMutualFundId());
		dto.setEmpInvestmentId(investment.getEmpInvestmentId());
		dto.setEmployeeId(investment.getEmployee().getUserId());
		dto.setEmployeeName(investment.getEmployee().getFirstName() + " " + investment.getEmployee().getLastName());
		dto.setInvestmentType(investment.getInvestmentType());
		dto.setDeclaredAmount(investment.getDeclaredAmount());
		dto.setComplianceStatus(investment.getComplianceStatus());
		dto.setReviewNote(investment.getReviewNote());
		dto.setDeclaredAt(investment.getDeclaredAt());
		dto.setUpdatedAt(investment.getUpadtedAt());

// ✅ reviewer is a Finance/Admin AppUser — NOT an Employee
		if (investment.getReviewedBy() != null) {
			appUserRepository.findById(investment.getReviewedBy())
					.ifPresent(reviewer -> dto.setReviewedBy(reviewer.getUsername()));
		}

// mutual fund info
		if (investment.getMutualFund() != null) {
			dto.setFundName(investment.getMutualFund().getFundName());
			dto.setFundCode(investment.getMutualFund().getFundCode());
		}

// security info
		if (investment.getSecurityName() != null) {
			dto.setSecurityName(investment.getSecurityName());
		}

		return dto;
	}

	@Override
	public Page<EmployeeInvestmentResponseDTO> getInvestmentsByEmployee(Long employeeId, int page, int size) throws ResourceNotFoundException {
		employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
		Pageable pageable = PageRequest.of(page, size, Sort.by("declaredAt").descending());
		return employeeInvestmentRepository.findByEmployee_UserId(employeeId, pageable).map(this::mapToResponse);
	}

}
