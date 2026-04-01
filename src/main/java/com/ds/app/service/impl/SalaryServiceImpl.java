package com.ds.app.service.impl;

import com.ds.app.dto.request.SalaryJobRequestDTO;
import com.ds.app.dto.response.SalaryJobResponseDTO;
import com.ds.app.dto.response.SalaryRecordResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.entity.EmployeeBankAccount;
import com.ds.app.entity.SalaryJob;
import com.ds.app.entity.SalaryRecord;
import com.ds.app.enums.JobStatus;
import com.ds.app.enums.PaymentStatus;
import com.ds.app.enums.Status;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.EmployeeBankAccountRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.SalaryJobRepository;
import com.ds.app.repository.SalaryRecordRepository;
import com.ds.app.service.EmailService;
import com.ds.app.service.SalaryService;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SalaryServiceImpl implements SalaryService {

	@Autowired
	SalaryJobRepository salaryJobRepository;
	@Autowired
	SalaryRecordRepository salaryRecordRepository;
	@Autowired
	EmployeeRepository employeeRepository;
	@Autowired
	EmployeeBankAccountRepository bankAccountRepository;
	@Autowired
	EmailService emailService;

	@Override
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
	        return salaryJobRepository.findAll(pageable)
	                .map(this::mapJobToResponse);
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
	        return salaryRecordRepository.findAll(pageable)
	                .map(this::mapRecordToResponse);
	    }

	// ── Process Salary Job ───────────────────────────────────────────────────
	    // called by scheduler every minute — checks for due jobs

	    @Override
	    @Transactional
	    public void processSalaryJob(Long jobId) throws ResourceNotFoundException {

	        SalaryJob job = salaryJobRepository.findById(jobId)
	                .orElseThrow(() -> new ResourceNotFoundException(
	                        "Salary job not found with id: " + jobId));

	        // mark job as running
	        job.setJobStatus(JobStatus.RUNNING);
	        salaryJobRepository.save(job);

	        // fetch all active non-deleted employees
	        List<Employee> employees = employeeRepository.findByStatus(Status.ACTIVE);

	        int total   = employees.size();
	        int success = 0;
	        int failed  = 0;

	        for (Employee employee : employees) {

	            try {
	                // skip if salary already credited this month
	                if (salaryRecordRepository.existsByEmployee_UserIdAndSalaryMonth(
	                        employee.getUserId(), job.getTargetMonth())) {
	                    total--;  // don't count as a failure — just skip
	                    continue;
	                }

	                // fetch bank account
	                EmployeeBankAccount bankAccount =
	                        bankAccountRepository.findByEmployee_UserId(
	                                employee.getUserId()).orElse(null);

	                if (bankAccount == null) {
	                    failed++;
	                    continue;  // no bank account — skip this employee
	                }

	                // calculate salary
	                Double grossSalary = Double.valueOf(
	                        employee.getCurrentSalary());
	                
	                // fixed deduction — 10% TDS for now
	                // TODO: fetch actual insurance premium from Insurance module
	                Double deductions = grossSalary*0.10;

	                Double netSalary = grossSalary-deductions;

	                // create salary record
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

	                // send email
	                emailService.sendSalaryCreditEmail(
	                        employee.getEmail(),       // email field
	                        employee.getFirstName() + " " + employee.getLastName(),
	                        job.getTargetMonth().toString(),
	                        netSalary.doubleValue(),
	                        maskAccount(bankAccount.getAccountNumber())
	                );

	                success++;

	            } catch (Exception e) {
	                failed++;
	                System.err.println("Salary processing failed for employee "
	                        + employee.getUserId() + ": " + e.getMessage());
	            }
	        }

	        // update job counts and status
	        job.setTotalEmployees(total);
	        job.setSuccessCount(success);
	        job.setFailureCount(failed);
	        job.setJobStatus(failed == 0 ? JobStatus.COMPLETED : JobStatus.FAILED);
	        salaryJobRepository.save(job);

	        // notify Finance team
	        // TODO: replace with actual Finance user email
	        emailService.sendSalaryJobCompletedEmail(
	                "sharmayatin0882@gmail.com",
	                job.getJobName(),
	                total, success, failed
	        );
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
	            dto.setBankAccountMasked(
	                    maskAccount(record.getBankAccount().getAccountNumber()));
	            dto.setBankName(record.getBankAccount().getBank().getBankName());
	        }

	        return dto;
	    }
	

}
