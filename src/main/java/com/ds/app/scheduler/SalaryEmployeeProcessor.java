package com.ds.app.scheduler;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeBankAccount;
import com.ds.app.entity.SalaryJob;
import com.ds.app.entity.SalaryProcessingLog;
import com.ds.app.entity.SalaryRecord;
import com.ds.app.enums.BankStatus;
import com.ds.app.enums.BankValidationStatus;
import com.ds.app.enums.PaymentStatus;
import com.ds.app.enums.SalaryProcessingStatus;
import com.ds.app.enums.SalarySkipReason;
import com.ds.app.repository.EmployeeBankAccountRepository;
import com.ds.app.repository.SalaryJobRepository;
import com.ds.app.repository.SalaryProcessingLogRepository;
import com.ds.app.repository.SalaryRecordRepository;
import com.ds.app.service.EmailService;

@Component
public class SalaryEmployeeProcessor {

    @Autowired SalaryRecordRepository salaryRecordRepository;
    @Autowired EmployeeBankAccountRepository bankAccountRepository;
    @Autowired SalaryProcessingLogRepository processingLogRepository;
    @Autowired SalaryJobRepository salaryJobRepository;
    @Autowired EmailService emailService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String processEmployee(Employee employee, Long jobId) {

        System.out.println("=================================================");
        System.out.println("PROCESSING EMPLOYEE: " + employee.getUserId() 
            + " | " + employee.getFirstName());
        System.out.println("JOB ID: " + jobId);

        try {
            SalaryJob job = salaryJobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
            System.out.println("✅ Job fetched: " + job.getJobName());

            // Check 1
            boolean alreadyPaid = salaryRecordRepository
                    .existsByEmployee_UserIdAndSalaryMonth(
                            employee.getUserId(), job.getTargetMonth());
            System.out.println("Already paid check: " + alreadyPaid);
            if (alreadyPaid) {
                saveLog(employee, job, SalaryProcessingStatus.SKIPPED,
                        SalarySkipReason.ALREADY_PAID,
                        "Salary already credited for " + job.getTargetMonth());
                return "skipped";
            }

            // Check 2
            System.out.println("Fetching bank account for employee: " + employee.getUserId());
            Optional<EmployeeBankAccount> bankOpt =
                    bankAccountRepository.findByEmployee_UserIdWithBank(employee.getUserId());
            System.out.println("Bank account present: " + bankOpt.isPresent());
            if (bankOpt.isEmpty()) {
                saveLog(employee, job, SalaryProcessingStatus.SKIPPED,
                        SalarySkipReason.NO_BANK_ACCOUNT,
                        "Employee has not registered a bank account");
                return "failed";
            }

            EmployeeBankAccount bankAccount = bankOpt.get();
            System.out.println("Bank account: " + bankAccount.getAccountNumber());
            System.out.println("Validation status: " + bankAccount.getValidationStatus());
            System.out.println("Bank status: " + bankAccount.getBank().getStatus());

            // Check 3
            if (bankAccount.getValidationStatus() == BankValidationStatus.PENDING) {
                saveLog(employee, job, SalaryProcessingStatus.SKIPPED,
                        SalarySkipReason.BANK_ACCOUNT_PENDING,
                        "Bank account is still pending Finance approval");
                return "failed";
            }

            // Check 4
            if (bankAccount.getValidationStatus() == BankValidationStatus.REJECTED) {
                saveLog(employee, job, SalaryProcessingStatus.SKIPPED,
                        SalarySkipReason.BANK_ACCOUNT_REJECTED,
                        "Bank account was rejected: " + bankAccount.getReviewNote());
                return "failed";
            }

            // Check 5
            if (bankAccount.getBank().getStatus() == BankStatus.BLACKLISTED) {
                saveLog(employee, job, SalaryProcessingStatus.SKIPPED,
                        SalarySkipReason.BANK_BLACKLISTED,
                        "Bank blacklisted: " + bankAccount.getBank().getBankName());
                return "failed";
            }

            // Credit salary
            Double grossSalary = employee.getCurrentSalary();
            Double deductions  = grossSalary * 0.10;
            Double netSalary   = grossSalary - deductions;
            System.out.println("Gross: " + grossSalary + " | Net: " + netSalary);

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

            System.out.println("Saving salary record...");
            salaryRecordRepository.save(record);
            System.out.println("✅ Salary record saved");

            System.out.println("Saving success log...");
            saveLog(employee, job, SalaryProcessingStatus.SUCCESS, null,
                    "Salary of Rs." + netSalary + " credited successfully");
            System.out.println("✅ Success log saved");

            try {
                emailService.sendSalaryCreditEmail(
                        employee.getUsername(),
                        employee.getFirstName() + " " + employee.getLastName(),
                        job.getTargetMonth().toString(),
                        netSalary,
                        maskAccount(bankAccount.getAccountNumber())
                );
                System.out.println("✅ Email sent");
            } catch (Exception e) {
                System.out.println("⚠️ Email failed (salary still credited): " + e.getMessage());
            }

            System.out.println("✅ RETURNING SUCCESS for employee: " + employee.getUserId());
            return "success";

        } catch (Exception e) {
            System.out.println("❌ EXCEPTION for employee " + employee.getUserId());
            System.out.println("❌ Message: " + e.getMessage());
            System.out.println("❌ Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "null"));
            e.printStackTrace();
            throw e; // rethrow so outer catch counts it as failed
        }
    }

    private void saveLog(Employee employee, SalaryJob job,
                         SalaryProcessingStatus status,
                         SalarySkipReason reason, String details) {
        System.out.println("Saving log: " + status + " | " + details);
        SalaryProcessingLog log = SalaryProcessingLog.builder()
                .employee(employee).salaryJob(job)
                .salaryMonth(job.getTargetMonth())
                .processingStatus(status)
                .skipReason(reason).details(details)
                .build();
        processingLogRepository.save(log);
        System.out.println("✅ Log saved");
    }

    private String maskAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) return "****";
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}
