package com.ds.app.repository;

import com.ds.app.entity.SalaryProcessingLog;
import com.ds.app.enums.SalaryProcessingStatus;
import com.ds.app.enums.SalarySkipReason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryProcessingLogRepository
        extends JpaRepository<SalaryProcessingLog, Long> {

    // all logs for one job
    Page<SalaryProcessingLog> findBySalaryJob_Id(
            Long jobId, Pageable pageable);

    // all failed/skipped for one job
    Page<SalaryProcessingLog> findBySalaryJob_IdAndProcessingStatus(
            Long jobId, SalaryProcessingStatus status, Pageable pageable);

    // all logs for one employee
    Page<SalaryProcessingLog> findByEmployee_UserId(
            Long employeeId, Pageable pageable);

    // all logs by skip reason — e.g. how many have no bank account	
    Page<SalaryProcessingLog> findBySkipReason(
            SalarySkipReason reason, Pageable pageable);
}