package com.ds.app.service;
import com.ds.app.dto.request.SalaryJobRequestDTO;
import com.ds.app.dto.response.SalaryJobResponseDTO;
import com.ds.app.dto.response.SalaryRecordResponseDTO;
import com.ds.app.exception.ResourceNotFoundException;

import org.springframework.data.domain.Page;

public interface SalaryService {

    SalaryJobResponseDTO scheduleJob(SalaryJobRequestDTO dto, Long createdBy);

    SalaryJobResponseDTO getJobById(Long id) throws ResourceNotFoundException;

    Page<SalaryJobResponseDTO> getAllJobs(int page, int size);

    Page<SalaryRecordResponseDTO> getSalaryRecordsByEmployee(Long employeeId, int page, int size) throws ResourceNotFoundException;

    Page<SalaryRecordResponseDTO> getAllSalaryRecords(int page, int size);

    void processSalaryJob(Long jobId) throws ResourceNotFoundException;  // called by scheduler
}