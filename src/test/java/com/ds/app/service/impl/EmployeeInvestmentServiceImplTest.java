package com.ds.app.service.impl;

import com.ds.app.dto.request.EmployeeInvestmentRequestDTO;
import com.ds.app.dto.request.InvestmentReviewRequestDTO;
import com.ds.app.dto.response.EmployeeInvestmentResponseDTO;
import com.ds.app.entity.*;
import com.ds.app.enums.*;
import com.ds.app.exception.*;
import com.ds.app.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeInvestmentServiceImplTest {

    @Mock private EmployeeInvestmentRepository employeeInvestmentRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private FinanceInvestmentRepository companyMutualFundRepository;

    @InjectMocks
    private EmployeeInvestmentServiceImpl investmentService;

    private Employee mockEmployee;
    private Employee mockEmployeeWithCompany;
    private FinanceInvestment fund;

    @BeforeEach
    void setUp() {

        mockEmployee = new Employee();
        mockEmployee.setUserId(1L);
        mockEmployee.setFirstName("Rahul");
        mockEmployee.setLastName("Verma");
//        mockEmployee.setC(null); // no company

        mockEmployeeWithCompany = new Employee();
        mockEmployeeWithCompany.setUserId(2L);
        mockEmployeeWithCompany.setFirstName("Sneha");
        mockEmployeeWithCompany.setLastName("Patel");
//        mockEmployeeWithCompany.setCompanyId(1L); // has company

        fund = new FinanceInvestment();
        fund.setMutualFundId(1L);
        fund.setFundName("ICICI Fund");
        fund.setFundCode("INF123");
        fund.setStatus(FundStatus.WHITELISTED);
    }

    // ── compliance tests ─────────────────────────────

    @Test
    void declareInvestment_noCompany_nonCompliant() throws InvestmentComplianceException, ResourceNotFoundException {

        EmployeeInvestmentRequestDTO dto =
                new EmployeeInvestmentRequestDTO(
                        InvestmentType.DIRECT_EQUITY,
                        null,
                        "Infosys",
                       30000.0);

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(mockEmployee));

        when(employeeInvestmentRepository.existsByEmployee_UserIdAndSecurityName(1L, "Infosys"))
                .thenReturn(false);

        when(employeeInvestmentRepository.save(any()))
                .thenAnswer(i -> {
                    EmployeeInvestment inv = i.getArgument(0);
                    inv.setEmpInvestmentId(1L);
                    return inv;
                });

        EmployeeInvestmentResponseDTO result =
                investmentService.declareInvestment(1L, dto);

        assertThat(result.getComplianceStatus())
                .isEqualTo(ComplianceStatus.NON_COMPLIANT);
    }

    @Test
    void declareInvestment_withCompany_directEquity_nonCompliant() throws InvestmentComplianceException, ResourceNotFoundException {

        EmployeeInvestmentRequestDTO dto =
                new EmployeeInvestmentRequestDTO(
                        InvestmentType.DIRECT_EQUITY,
                        null,
                        "Infosys",
                        100000.0);

        when(employeeRepository.findById(2L))
                .thenReturn(Optional.of(mockEmployeeWithCompany));

        when(employeeInvestmentRepository.existsByEmployee_UserIdAndSecurityName(2L, "Infosys"))
                .thenReturn(false);

        when(employeeInvestmentRepository.save(any()))
                .thenAnswer(i -> {
                    EmployeeInvestment inv = i.getArgument(0);
                    inv.setEmpInvestmentId(1L);
                    return inv;
                });

        EmployeeInvestmentResponseDTO result =
                investmentService.declareInvestment(2L, dto);

        assertThat(result.getComplianceStatus())
                .isEqualTo(ComplianceStatus.NON_COMPLIANT);
    }

    @Test
    void declareInvestment_mutualFund_alwaysNonCompliant() throws InvestmentComplianceException, ResourceNotFoundException {

        EmployeeInvestmentRequestDTO dto =
                new EmployeeInvestmentRequestDTO(
                        InvestmentType.MUTUAL_FUND,
                        1L,
                        null,
                        50000.0);

        when(employeeRepository.findById(2L))
                .thenReturn(Optional.of(mockEmployeeWithCompany));

        when(companyMutualFundRepository.findById(1L))
                .thenReturn(Optional.of(fund));

        when(employeeInvestmentRepository
                .existsByEmployee_UserIdAndMutualFund_MutualFundId(2L, 1L))
                .thenReturn(false);

        when(employeeInvestmentRepository.save(any()))
                .thenAnswer(i -> {
                    EmployeeInvestment inv = i.getArgument(0);
                    inv.setEmpInvestmentId(1L);
                    return inv;
                });

        EmployeeInvestmentResponseDTO result =
                investmentService.declareInvestment(2L, dto);

        assertThat(result.getComplianceStatus())
                .isEqualTo(ComplianceStatus.NON_COMPLIANT);
    }

    // ── validation tests ─────────────────────────────

    @Test
    void declareInvestment_mutualFundWithoutFundId_throwsException() {

        EmployeeInvestmentRequestDTO dto =
                new EmployeeInvestmentRequestDTO(
                        InvestmentType.MUTUAL_FUND,
                        null,
                        null,
                        10000.0);

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(mockEmployee));

        assertThatThrownBy(() ->
                investmentService.declareInvestment(1L, dto))
                .isInstanceOf(InvestmentComplianceException.class)
                .hasMessageContaining("Fund ID is required");
    }

    @Test
    void declareInvestment_directEquityWithoutSecurityName_throwsException() {

        EmployeeInvestmentRequestDTO dto =
                new EmployeeInvestmentRequestDTO(
                        InvestmentType.DIRECT_EQUITY,
                        null,
                        null,
                        10000.0);

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(mockEmployee));

        assertThatThrownBy(() ->
                investmentService.declareInvestment(1L, dto))
                .isInstanceOf(InvestmentComplianceException.class)
                .hasMessageContaining("Security name is required");
    }

    @Test
    void declareInvestment_duplicateFund_throwsException() {

        EmployeeInvestmentRequestDTO dto =
                new EmployeeInvestmentRequestDTO(
                        InvestmentType.MUTUAL_FUND,
                        1L,
                        null,
                        50000.0);

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(mockEmployee));

        when(companyMutualFundRepository.findById(1L))
                .thenReturn(Optional.of(fund));

        when(employeeInvestmentRepository
                .existsByEmployee_UserIdAndMutualFund_MutualFundId(1L, 1L))
                .thenReturn(true);

        assertThatThrownBy(() ->
                investmentService.declareInvestment(1L, dto))
                .isInstanceOf(InvestmentComplianceException.class)
                .hasMessageContaining("already declared");
    }

    // ── review tests ─────────────────────────────

   

    @Test
    void reviewInvestment_alreadyCompliant_throwsException() {

        EmployeeInvestment investment = new EmployeeInvestment();
        investment.setEmpInvestmentId(1L);
        investment.setComplianceStatus(ComplianceStatus.COMPLIANT);

        InvestmentReviewRequestDTO dto =
                new InvestmentReviewRequestDTO(
                        ComplianceStatus.NON_COMPLIANT,
                        "Change");

        when(employeeInvestmentRepository.findById(1L))
                .thenReturn(Optional.of(investment));

        assertThatThrownBy(() ->
                investmentService.reviewInvestment(1L, dto, 5L))
                .isInstanceOf(InvestmentComplianceException.class)
                .hasMessageContaining("already compliant");
    }
}