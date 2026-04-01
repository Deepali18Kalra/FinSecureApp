package com.ds.app.controller;
import org.springframework.web.bind.annotation.RestController;

import com.ds.app.dto.request.SalaryJobRequestDTO;
import com.ds.app.dto.response.SalaryJobResponseDTO;
import com.ds.app.dto.response.SalaryRecordResponseDTO;
import com.ds.app.entity.MyUserDetails;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.service.SalaryService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

	    private Long getLoggedInUserId() {
	        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder
	                .getContext().getAuthentication().getPrincipal();
	        return userDetails.getUser().getUserId();
	    }

	    // Finance schedules a salary job
	    @PostMapping("/schedule")
	    public SalaryJobResponseDTO scheduleJob(
	            @Valid @RequestBody SalaryJobRequestDTO dto) {
	        return salaryService.scheduleJob(dto, getLoggedInUserId());
	    }

	    // Get one job by id
	    @GetMapping("/jobs/{id}")
	    public SalaryJobResponseDTO getJobById(@PathVariable Long id) throws ResourceNotFoundException {
	        return salaryService.getJobById(id);
	    }

	    // Get all jobs paginated
	    @GetMapping("/jobs")
	    public Page<SalaryJobResponseDTO> getAllJobs(
	            @RequestParam(defaultValue = "0")  int page,
	            @RequestParam(defaultValue = "10") int size) {
	        return salaryService.getAllJobs(page, size);
	    }

	    // Get salary records of one employee
	    @GetMapping("/records/employee/{employeeId}")
	    public Page<SalaryRecordResponseDTO> getRecordsByEmployee(
	            @PathVariable Long employeeId,
	            @RequestParam(defaultValue = "0")  int page,
	            @RequestParam(defaultValue = "10") int size) throws ResourceNotFoundException {
	        return salaryService.getSalaryRecordsByEmployee(employeeId, page, size);
	    }

	    // Get all salary records
	    @GetMapping("/records")
	    public Page<SalaryRecordResponseDTO> getAllRecords(
	            @RequestParam(defaultValue = "0")  int page,
	            @RequestParam(defaultValue = "10") int size) {
	        return salaryService.getAllSalaryRecords(page, size);
	    }

	    // Manually trigger a job — useful for testing
	    @PostMapping("/jobs/{id}/trigger")
	    public String triggerJob(@PathVariable Long id) throws ResourceNotFoundException {
	        salaryService.processSalaryJob(id);
	        return "Job triggered for id: " + id;
	    }
}
