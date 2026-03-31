package com.ds.app.repository;

import com.ds.app.entity.TimesheetEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ITimesheetEntryRepository extends JpaRepository<TimesheetEntry, Long> {

    List<TimesheetEntry> findByTimesheetEmployeeUserIdAndDateBetweenOrderByDateAsc(
            Long employeeId, LocalDate startDate, LocalDate endDate
    );

    Optional<TimesheetEntry> findByTimesheetEntryIdAndTimesheetEmployeeUserId(
            Long entryId, Long employeeId
    );

    List<TimesheetEntry> findByTimesheetTimesheetIdOrderByDateAsc(Long timesheetId);
}