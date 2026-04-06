
package com.ds.app.service.impl;

import com.ds.app.dto.request.SalaryJobRequestDTO;
import com.ds.app.dto.response.SalaryJobResponseDTO;
import com.ds.app.entity.*;
import com.ds.app.enums.*;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.*;
import com.ds.app.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaryServiceImplTest {

    @Mock private SalaryJobRepository salaryJobRepository;
    @Mock private SalaryRecordRepository salaryRecordRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private EmployeeBankAccountRepository bankAccountRepository;
    @Mock private SalaryProcessingLogRepository processingLogRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private SalaryServiceImpl salaryService;

    private SalaryJob mockJob;
    private Employee mockEmployee;
    private FinanceBankAccount mockBank;
    private EmployeeBankAccount mockBankAccount;

    @BeforeEach
    void setUp() {

        mockJob = new SalaryJob();
        mockJob.setId(1L);
        mockJob.setJobName("SALARY_MARCH_2025");
        mockJob.setTargetMonth(java.time.YearMonth.of(2025, 3));
        mockJob.setJobStatus(JobStatus.SCHEDULED);
        mockJob.setCreatedBy(1L);

        mockEmployee = new Employee();
        mockEmployee.setUserId(2L);
        mockEmployee.setFirstName("Rahul");
        mockEmployee.setLastName("Verma");
        mockEmployee.setCurrentSalary(60000.0);
        mockEmployee.setStatus(Status.ACTIVE);

        mockBank = new FinanceBankAccount();
        mockBank.setBankId(1L);
        mockBank.setBankName("ICICI Bank");
        mockBank.setBankCode("ICIC");
        mockBank.setStatus(BankStatus.WHITELISTED);

        mockBankAccount = EmployeeBankAccount.builder()
                .empBankId(1L)
                .employee(mockEmployee)
                .bank(mockBank) // ✅ CRITICAL FIX
                .accountNumber("123456781234")
                .validationStatus(BankValidationStatus.APPROVED)
                .modifiedToday(0)
                .build();
    }

    // ── SUCCESS CASE ─────────────────────────────

    @Test
    void processSalaryJob_success_creditsSalary() throws Exception {

        when(salaryJobRepository.findById(1L))
                .thenReturn(Optional.of(mockJob));

        when(employeeRepository.findByStatus(Status.ACTIVE))
                .thenReturn(List.of(mockEmployee));

        when(salaryRecordRepository.existsByEmployee_UserIdAndSalaryMonth(
                2L, mockJob.getTargetMonth()))
                .thenReturn(false);

        when(bankAccountRepository.findByEmployee_UserIdWithBank(2L))
                .thenReturn(Optional.of(mockBankAccount));

        when(salaryRecordRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        salaryService.processSalaryJob(1L);

        verify(salaryRecordRepository).save(any(SalaryRecord.class));

        verify(processingLogRepository).save(argThat(log ->
                log.getProcessingStatus() == SalaryProcessingStatus.SUCCESS
        ));
    }
    // ── NO BANK ACCOUNT ─────────────────────────────

    @Test
    void processSalaryJob_bankAccountPending_logsFailure1() throws Exception {

        // ✅ make validation pending
        mockBankAccount.setValidationStatus(BankValidationStatus.PENDING);

        when(salaryJobRepository.findById(1L))
                .thenReturn(Optional.of(mockJob));

        when(employeeRepository.findByStatus(Status.ACTIVE))
                .thenReturn(List.of(mockEmployee));

        when(salaryRecordRepository.existsByEmployee_UserIdAndSalaryMonth(
                2L, mockJob.getTargetMonth()))
                .thenReturn(false);

        
        when(bankAccountRepository.findByEmployee_UserIdWithBank(2L))
                .thenReturn(Optional.of(mockBankAccount));

        salaryService.processSalaryJob(1L);

        verify(processingLogRepository).save(argThat(log ->
                log.getSkipReason() == SalarySkipReason.BANK_ACCOUNT_PENDING &&
                log.getProcessingStatus() == SalaryProcessingStatus.SKIPPED
        ));

        verify(salaryRecordRepository, never()).save(any());
    }

    // ── BANK PENDING ─────────────────────────────

    @Test
    void processSalaryJob_bankAccountPending_logsFailure() throws Exception {

        // Arrange
        mockBankAccount.setValidationStatus(BankValidationStatus.PENDING);

        when(salaryJobRepository.findById(1L))
                .thenReturn(Optional.of(mockJob));

        when(employeeRepository.findByStatus(Status.ACTIVE))
                .thenReturn(List.of(mockEmployee));

        when(salaryRecordRepository.existsByEmployee_UserIdAndSalaryMonth(
                2L, mockJob.getTargetMonth()))
                .thenReturn(false);

        // ✅ THIS LINE MUST MATCH SERVICE METHOD NAME EXACTLY
        when(bankAccountRepository.findByEmployee_UserIdWithBank(2L))
                .thenReturn(Optional.of(mockBankAccount));

        // Act
        salaryService.processSalaryJob(1L);

        // Assert
        verify(processingLogRepository).save(argThat(log ->
                log.getSkipReason() == SalarySkipReason.BANK_ACCOUNT_PENDING &&
                log.getProcessingStatus() == SalaryProcessingStatus.SKIPPED
        ));

        verify(salaryRecordRepository, never()).save(any());
    }


    // ── BANK BLACKLISTED ─────────────────────────────

    @Test
    void processSalaryJob_bankBlacklisted_logsFailure() throws Exception {

        mockBank.setStatus(BankStatus.BLACKLISTED);

        when(salaryJobRepository.findById(1L))
                .thenReturn(Optional.of(mockJob));

        when(employeeRepository.findByStatus(Status.ACTIVE))
                .thenReturn(List.of(mockEmployee));

        when(salaryRecordRepository.existsByEmployee_UserIdAndSalaryMonth(
                2L, mockJob.getTargetMonth()))
                .thenReturn(false);

        // ✅ Important: bank account MUST exist
        when(bankAccountRepository.findByEmployee_UserIdWithBank(2L))
                .thenReturn(Optional.of(mockBankAccount));

        salaryService.processSalaryJob(1L);

        verify(processingLogRepository).save(argThat(log ->
                log.getSkipReason() == SalarySkipReason.BANK_BLACKLISTED
                && log.getProcessingStatus() == SalaryProcessingStatus.SKIPPED
        ));

        verify(salaryRecordRepository, never()).save(any());
    }

    // ── SALARY CALCULATION ─────────────────────────────

    @Test
    void processSalaryJob_salaryCalculation_correct() throws Exception {

        when(salaryJobRepository.findById(1L))
                .thenReturn(Optional.of(mockJob));

        when(employeeRepository.findByStatus(Status.ACTIVE))
                .thenReturn(List.of(mockEmployee));

        when(salaryRecordRepository.existsByEmployee_UserIdAndSalaryMonth(
                2L, mockJob.getTargetMonth()))
                .thenReturn(false);

        when(bankAccountRepository.findByEmployee_UserIdWithBank(2L))
                .thenReturn(Optional.of(mockBankAccount));

        when(salaryRecordRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        salaryService.processSalaryJob(1L);

        verify(salaryRecordRepository).save(argThat(record ->
                record.getGrossSalary() == 60000.0 &&
                record.getDeductions() == 6000.0 &&
                record.getNetSalary() == 54000.0
        ));
    }
}