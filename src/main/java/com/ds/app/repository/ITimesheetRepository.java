package com.ds.app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ds.app.entity.Timesheet;
import com.ds.app.enums.TimesheetStatus;

@Repository
public interface ITimesheetRepository extends JpaRepository<Timesheet, Long> {

    Optional<Timesheet> findByEmployeeUserIdAndMonthAndYear(Long employeeId, Integer month, Integer year);

    Optional<Timesheet> findByTimesheetIdAndEmployeeUserId(Long timesheetId, Long employeeId);

    Page<Timesheet> findByEmployeeHrUserIdAndStatus(Long hrId, TimesheetStatus status, Pageable pageable);

    Page<Timesheet> findByEmployeeHrUserIdAndMonthAndYear(Long hrId, Integer month, Integer year, Pageable pageable);
}