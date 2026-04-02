package com.ds.app.service.impl;

import com.ds.app.dto.request.SalaryJobRequestDTO;
import com.ds.app.dto.response.SalaryJobResponseDTO;
import com.ds.app.dto.response.SalaryRecordResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeBankAccount;
import com.ds.app.entity.SalaryJob;
import com.ds.app.entity.SalaryProcessingLog;
import com.ds.app.entity.SalaryRecord;
import com.ds.app.enums.BankStatus;
import com.ds.app.enums.BankValidationStatus;
import com.ds.app.enums.JobStatus;
import com.ds.app.enums.PaymentStatus;
import com.ds.app.enums.SalaryProcessingStatus;
import com.ds.app.enums.SalarySkipReason;
import com.ds.app.enums.Status;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.EmployeeBankAccountRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.SalaryJobRepository;
import com.ds.app.repository.SalaryProcessingLogRepository;
import com.ds.app.repository.SalaryRecordRepository;
import com.ds.app.scheduler.SalaryEmployeeProcessor;
import com.ds.app.service.EmailService;
import com.ds.app.service.SalaryService;

//import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalaryServiceImpl implements SalaryService {

    @Autowired SalaryJobRepository salaryJobRepository;
    @Autowired SalaryRecordRepository salaryRecordRepository;
    @Autowired EmployeeRepository employeeRepository;
    @Autowired EmployeeBankAccountRepository bankAccountRepository;
    @Autowired EmailService emailService;
    @Autowired SalaryProcessingLogRepository processingLogRepository;

    @Override
    @Transactional(noRollbackFor = Exception.class)
    public SalaryJobResponseDTO scheduleJob(SalaryJobRequestDTO dto, Long createdBy) {
        YearMonth targetMonth = YearMonth.parse(dto.getTargetMonth());
        SalaryJob job = new SalaryJob();
        job.setJobName(dto.getJobName());
        job.setScheduledDateTime(dto.getScheduledDateTime());
        job.setTargetMonth(targetMonth);
        job.setJobStatus(JobStatus.SCHEDULED);
        job.setCreatedBy(createdBy);
        return mapJobToResponse(salaryJobRepository.save(job));
    }

    @Override
    public SalaryJobResponseDTO getJobById(Long id) throws ResourceNotFoundException {
        SalaryJob job = salaryJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Salary job not found with id: " + id));
        return mapJobToResponse(job);
    }

    @Override
    public Page<SalaryJobResponseDTO> getAllJobs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return salaryJobRepository.findAll(pageable).map(this::mapJobToResponse);
    }

    @Override
    public Page<SalaryRecordResponseDTO> getSalaryRecordsByEmployee(
            Long employeeId, int page, int size) throws ResourceNotFoundException {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + employeeId));
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("creditedAt").descending());
        return salaryRecordRepository.findByEmployee_UserId(employeeId, pageable)
                .map(this::mapRecordToResponse);
    }

    @Override
    public Page<SalaryRecordResponseDTO> getAllSalaryRecords(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("creditedAt").descending());
        return salaryRecordRepository.findAll(pageable).map(this::mapRecordToResponse);
    }

    @Override
    @Transactional(noRollbackFor = Exception.class)
    public void processSalaryJob(Long jobId) throws ResourceNotFoundException {

        SalaryJob job = salaryJobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Salary job not found: " + jobId));

        job.setJobStatus(JobStatus.RUNNING);
        salaryJobRepository.save(job);

        List<Employee> employees = employeeRepository.findByStatus(Status.ACTIVE);

        int total   = employees.size();
        int success = 0;
        int skipped = 0;
        int failed  = 0;

        for (Employee employee : employees) {
            try {

                // Check 1: Already paid
                if (salaryRecordRepository.existsByEmployee_UserIdAndSalaryMonth(
                        employee.getUserId(), job.getTargetMonth())) {
                    saveLog(employee, job, SalaryProcessingStatus.SKIPPED,
                            SalarySkipReason.ALREADY_PAID,
                            "Salary already credited for " + job.getTargetMonth());
                    skipped++;
                    total--;
                    continue;
                }

                // Check 2: Has bank account
                Optional<EmployeeBankAccount> bankOpt =
                        bankAccountRepository.findByEmployee_UserIdWithBank(employee.getUserId());
                if (bankOpt.isEmpty()) {
                    saveLog(employee, job, SalaryProcessingStatus.SKIPPED,
                            SalarySkipReason.NO_BANK_ACCOUNT,
                            "Employee has not registered a bank account");
                    skipped++;
                    failed++;
                    continue;
                }

                EmployeeBankAccount bankAccount = bankOpt.get();

                // Check 3: Pending
                if (bankAccount.getValidationStatus() == BankValidationStatus.PENDING) {
                    saveLog(employee, job, SalaryProcessingStatus.SKIPPED,
                            SalarySkipReason.BANK_ACCOUNT_PENDING,
                            "Bank account is still pending Finance approval");
                    skipped++;
                    failed++;
                    continue;
                }

                // Check 4: Rejected
                if (bankAccount.getValidationStatus() == BankValidationStatus.REJECTED) {
                    saveLog(employee, job, SalaryProcessingStatus.SKIPPED,
                            SalarySkipReason.BANK_ACCOUNT_REJECTED,
                            "Bank account was rejected by Finance — reason: "
                            + bankAccount.getReviewNote());
                    skipped++;
                    failed++;
                    continue;
                }

                // Check 5: Bank blacklisted
                if (bankAccount.getBank().getStatus() == BankStatus.BLACKLISTED) {
                    saveLog(employee, job, SalaryProcessingStatus.SKIPPED,
                            SalarySkipReason.BANK_BLACKLISTED,
                            "Employee's bank " + bankAccount.getBank().getBankName()
                            + " has been blacklisted");
                    skipped++;
                    failed++;
                    continue;
                }

                // All checks passed — credit salary
                Double grossSalary = employee.getCurrentSalary();
                Double deductions  = grossSalary * 0.10;
                Double netSalary   = grossSalary - deductions;

                SalaryRecord record = new SalaryRecord();
                record.setEmployee(employee);
                record.setSalaryJob(job);
                record.setSalaryMonth(job.getTargetMonth());
                record.setGrossSalary(grossSalary);
                record.setDeductions(deductions);
                record.setNetSalary(netSalary);
                record.setBankAccount(bankAccount);
                record.setPaymentStatus(PaymentStatus.CREDITED);
                record.setCreditedAt(LocalDateTime.now());
                salaryRecordRepository.save(record);

                saveLog(employee, job, SalaryProcessingStatus.SUCCESS,
                        null,
                        "Salary of ₹" + netSalary + " credited successfully");

                try {
                    emailService.sendSalaryCreditEmail(
                            employee.getUsername(),
                            employee.getFirstName() + " " + employee.getLastName(),
                            job.getTargetMonth().toString(),
                            netSalary,
                            maskAccount(bankAccount.getAccountNumber())
                    );
                } catch (Exception e) {
                    System.err.println("Email failed for " + employee.getUsername()
                            + ": " + e.getMessage());
                }

                success++;

            } catch (Exception e) {
                saveLog(employee, job, SalaryProcessingStatus.FAILED,
                        SalarySkipReason.PROCESSING_ERROR,
                        "Unexpected error: " + e.getMessage());
                failed++;
            }
        }

        job.setTotalEmployees(total);
        job.setSuccessCount(success);
        job.setFailureCount(failed);
        job.setJobStatus(failed == 0 ? JobStatus.COMPLETED : JobStatus.FAILED);
        salaryJobRepository.save(job);

        emailService.sendSalaryJobCompletedEmail(
                "sharmayatin0882@gmail.com",
                job.getJobName(),
                total, success, failed, skipped
        );
    }

    private void saveLog(Employee employee, SalaryJob job,
                         SalaryProcessingStatus status,
                         SalarySkipReason reason, String details) {
        SalaryProcessingLog log = SalaryProcessingLog.builder()
                .employee(employee)
                .salaryJob(job)
                .salaryMonth(job.getTargetMonth())
                .processingStatus(status)
                .skipReason(reason)
                .details(details)
                .build();
        processingLogRepository.save(log);
    }

    private String maskAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) return "****";
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }

    private SalaryJobResponseDTO mapJobToResponse(SalaryJob job) {
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
        dto.setCreatedAt(job.getCreatedAt());
        return dto;
    }

    private SalaryRecordResponseDTO mapRecordToResponse(SalaryRecord record) {
        SalaryRecordResponseDTO dto = new SalaryRecordResponseDTO();
        dto.setId(record.getId());
        dto.setEmployeeId(record.getEmployee().getUserId());
        dto.setEmployeeName(record.getEmployee().getFirstName()
                + " " + record.getEmployee().getLastName());
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
}