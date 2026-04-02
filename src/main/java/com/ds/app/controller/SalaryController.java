package com.ds.app.controller;
import org.springframework.web.bind.annotation.RestController;

import com.ds.app.dto.request.SalaryJobRequestDTO;
import com.ds.app.dto.response.SalaryJobResponseDTO;
import com.ds.app.dto.response.SalaryProcessingLogResponseDTO;
import com.ds.app.dto.response.SalaryRecordResponseDTO;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.entity.SalaryProcessingLog;
import com.ds.app.enums.SalaryProcessingStatus;
import com.ds.app.enums.SalarySkipReason;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.repository.SalaryProcessingLogRepository;
import com.ds.app.service.SalaryService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
@RestController
@RequestMapping("/finsecure/finance/salary")
public class SalaryController {
	
	@Autowired
	 SalaryService salaryService;
	
	@Autowired
	SalaryProcessingLogRepository processingLogRepository;

	    private Long getLoggedInUserId() {
	        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
	                .getContext().getAuthentication().getPrincipal();
	        return userDetails.getUser().getUserId();
	    }

	    // Finance schedules a salary job
	    @PostMapping("/schedule")
	    @PreAuthorize("hasAuthority('FINANCE')")
	    public SalaryJobResponseDTO scheduleJob(
	            @Valid @RequestBody SalaryJobRequestDTO dto) {
	        return salaryService.scheduleJob(dto, getLoggedInUserId());
	    }

	    // Get one job by id
	    @GetMapping("/jobs/{id}")
	    @PreAuthorize("hasAuthority('FINANCE')")
	    public SalaryJobResponseDTO getJobById(@PathVariable Long id) throws ResourceNotFoundException {
	        return salaryService.getJobById(id);
	    }

	    // Get all jobs paginated
	    @GetMapping("/jobs")
	    @PreAuthorize("hasAuthority('FINANCE')")
	    public Page<SalaryJobResponseDTO> getAllJobs(
	            @RequestParam(defaultValue = "0")  int page,
	            @RequestParam(defaultValue = "10") int size) {
	        return salaryService.getAllJobs(page, size);
	    }

	    // Get salary records of one employee
	    @GetMapping("/records/employee/{employeeId}")
	    @PreAuthorize("hasAuthority('FINANCE')")
	    public Page<SalaryRecordResponseDTO> getRecordsByEmployee(
	            @PathVariable Long employeeId,
	            @RequestParam(defaultValue = "0")  int page,
	            @RequestParam(defaultValue = "10") int size) throws ResourceNotFoundException {
	        return salaryService.getSalaryRecordsByEmployee(employeeId, page, size);
	    }

	    // Get all salary records
	    @GetMapping("/records")
	    @PreAuthorize("hasAuthority('FINANCE')")
	    public Page<SalaryRecordResponseDTO> getAllRecords(
	            @RequestParam(defaultValue = "0")  int page,
	            @RequestParam(defaultValue = "10") int size) {
	        return salaryService.getAllSalaryRecords(page, size);
	    }

	    // Manually trigger a job — useful for testing
	    @PostMapping("/jobs/{id}/trigger")
	    @PreAuthorize("hasAuthority('FINANCE')")
	    public String triggerJob(@PathVariable Long id) throws ResourceNotFoundException {
	        salaryService.processSalaryJob(id);
	        return "Job triggered for id: " + id;
	    }
	 // all logs for a specific job — see who passed, failed, skipped
	    @PreAuthorize("hasAnyAuthority('FINANCE', 'ADMIN', 'HR')")
	    @GetMapping("/jobs/{jobId}/logs")
	    public Page<SalaryProcessingLogResponseDTO> getJobLogs(
	            @PathVariable Long jobId,
	            @RequestParam(defaultValue = "0")  int page,
	            @RequestParam(defaultValue = "10") int size,
	            @RequestParam(required = false) SalaryProcessingStatus status) {

	        Pageable pageable = PageRequest.of(page, size);

	        if (status != null) {
	            return processingLogRepository
	                    .findBySalaryJob_IdAndProcessingStatus(jobId, status, pageable)
	                    .map(this::mapLog);
	        }

	        return processingLogRepository
	                .findBySalaryJob_Id(jobId, pageable)
	                .map(this::mapLog);
	    }

	    // all logs for a specific employee across all jobs
	    @PreAuthorize("hasAnyAuthority('FINANCE', 'ADMIN', 'HR')")
	    @GetMapping("/logs/employee/{employeeId}")
	    public Page<SalaryProcessingLogResponseDTO> getEmployeeLogs(
	            @PathVariable Long employeeId,
	            @RequestParam(defaultValue = "0")  int page,
	            @RequestParam(defaultValue = "10") int size) {

	        Pageable pageable = PageRequest.of(page, size,
	                Sort.by("loggedAt").descending());

	        return processingLogRepository
	                .findByEmployee_UserId(employeeId, pageable)
	                .map(this::mapLog);
	    }

	    // all employees with a specific skip reason
	    // useful: "show me all employees with no bank account"
	    @PreAuthorize("hasAnyAuthority('FINANCE', 'ADMIN', 'HR')")
	    @GetMapping("/logs/reason/{reason}")
	    public Page<SalaryProcessingLogResponseDTO> getLogsByReason(
	            @PathVariable SalarySkipReason reason,
	            @RequestParam(defaultValue = "0")  int page,
	            @RequestParam(defaultValue = "10") int size) {

	        Pageable pageable = PageRequest.of(page, size,
	                Sort.by("loggedAt").descending());

	        return processingLogRepository
	                .findBySkipReason(reason, pageable)
	                .map(this::mapLog);
	    }

	    // mapper
	    private SalaryProcessingLogResponseDTO mapLog(SalaryProcessingLog log) {
	        SalaryProcessingLogResponseDTO dto = new SalaryProcessingLogResponseDTO();
	        dto.setId(log.getId());
	        dto.setEmployeeId(log.getEmployee().getUserId());
	        dto.setEmployeeName(log.getEmployee().getFirstName()
	                + " " + log.getEmployee().getLastName());
	        dto.setEmployeeCode(log.getEmployee().getEmployeeCode());
	        dto.setSalaryMonth(log.getSalaryMonth().toString());
	        dto.setProcessingStatus(log.getProcessingStatus());
	        dto.setSkipReason(log.getSkipReason());
	        dto.setDetails(log.getDetails());
	        dto.setLoggedAt(log.getLoggedAt());
	        return dto;
	    }
}
