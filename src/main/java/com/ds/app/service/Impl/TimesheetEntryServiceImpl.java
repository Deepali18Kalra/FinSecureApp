package com.ds.app.service.Impl;

import com.ds.app.dto.TimesheetEntryRequest;
import com.ds.app.dto.TimesheetEntryResponse;
import com.ds.app.entity.Employee;
import com.ds.app.entity.Timesheet;
import com.ds.app.entity.TimesheetEntry;
import com.ds.app.enums.TimesheetStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.mapper.TimesheetEntryMapper;
import com.ds.app.repository.ITimesheetEntryRepository;
import com.ds.app.repository.ITimesheetRepository;
import com.ds.app.service.ITimesheetEntryService;
import com.ds.app.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimesheetEntryServiceImpl implements ITimesheetEntryService {

    private final ITimesheetEntryRepository entryRepository;
    private final ITimesheetRepository timesheetRepository;
    private final TimesheetEntryMapper timesheetEntryMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public TimesheetEntryResponse addMyEntry(TimesheetEntryRequest request) {
        Employee me = securityUtils.getLoggedInEmployee();

        int month = request.getDate().getMonthValue();
        int year = request.getDate().getYear();

        Timesheet timesheet = timesheetRepository
                .findByEmployeeUserIdAndMonthAndYear(me.getUserId(), month, year)
                .orElseGet(() -> timesheetRepository.save(
                        Timesheet.builder()
                                .employee(me)
                                .month(month)
                                .year(year)
                                .status(TimesheetStatus.DRAFT)
                                .totalMonthlyHours(0.0)
                                .build()
                ));

        ensureEditableAndResetIfRejected(timesheet);

        TimesheetEntry entry = TimesheetEntry.builder()
                .timesheet(timesheet)
                .date(request.getDate())
                .taskDescription(request.getTaskDescription())
                .hoursWorked(request.getHoursWorked())
                .projectId(request.getProjectId())
                .projectName(request.getProjectName())
                .build();

        TimesheetEntry saved = entryRepository.save(entry);
        recalculateTotalHours(timesheet);

        return timesheetEntryMapper.mapToResponse(saved);
    }

    @Override
    public List<TimesheetEntryResponse> getMyEntries(Integer month, Integer year) {
        Employee me = securityUtils.getLoggedInEmployee();
        YearMonth ym = YearMonth.of(year, month);

        return entryRepository.findByTimesheetEmployeeUserIdAndDateBetweenOrderByDateAsc(
                        me.getUserId(),
                        ym.atDay(1),
                        ym.atEndOfMonth()
                ).stream()
                .map(timesheetEntryMapper::mapToResponse)
                .toList();
    }

    @Override
    public List<TimesheetEntryResponse> getMyEntriesByDateRange(LocalDate startDate, LocalDate endDate) {
        Employee me = securityUtils.getLoggedInEmployee();

        return entryRepository.findByTimesheetEmployeeUserIdAndDateBetweenOrderByDateAsc(
                        me.getUserId(), startDate, endDate
                ).stream()
                .map(timesheetEntryMapper::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public TimesheetEntryResponse updateMyEntry(Long entryId, TimesheetEntryRequest request) {
        Employee me = securityUtils.getLoggedInEmployee();

        TimesheetEntry existing = entryRepository.findByTimesheetEntryIdAndTimesheetEmployeeUserId(entryId, me.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Timesheet entry not found with id: " + entryId));

        Timesheet timesheet = existing.getTimesheet();
        ensureEditableAndResetIfRejected(timesheet);

        if (request.getDate().getMonthValue() != timesheet.getMonth()
                || request.getDate().getYear() != timesheet.getYear()) {
            throw new IllegalArgumentException("Entry date must remain within same timesheet month/year.");
        }

        existing.setDate(request.getDate());
        existing.setTaskDescription(request.getTaskDescription());
        existing.setHoursWorked(request.getHoursWorked());
        existing.setProjectId(request.getProjectId());
        existing.setProjectName(request.getProjectName());

        TimesheetEntry saved = entryRepository.save(existing);
        recalculateTotalHours(timesheet);

        return timesheetEntryMapper.mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteMyEntry(Long entryId) {
        Employee me = securityUtils.getLoggedInEmployee();

        TimesheetEntry existing = entryRepository.findByTimesheetEntryIdAndTimesheetEmployeeUserId(entryId, me.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Timesheet entry not found with id: " + entryId));

        Timesheet timesheet = existing.getTimesheet();
        ensureEditableAndResetIfRejected(timesheet);

        entryRepository.delete(existing);
        recalculateTotalHours(timesheet);
    }

    private void ensureEditableAndResetIfRejected(Timesheet timesheet) {
        if (timesheet.getStatus() == TimesheetStatus.SUBMITTED || timesheet.getStatus() == TimesheetStatus.APPROVED) {
            throw new IllegalStateException("Cannot modify entries. Timesheet is already submitted/approved.");
        }

        if (timesheet.getStatus() == TimesheetStatus.REJECTED) {
            timesheet.setStatus(TimesheetStatus.DRAFT);
            timesheet.setSubmittedAt(null);
            timesheet.setApprovedBy(null);
            timesheet.setApprovalDate(null);
            timesheet.setRejectionReason(null);
            timesheetRepository.save(timesheet);
        }
    }

    private void recalculateTotalHours(Timesheet timesheet) {
        double total = entryRepository.findByTimesheetTimesheetIdOrderByDateAsc(timesheet.getTimesheetId())
                .stream()
                .map(TimesheetEntry::getHoursWorked)
                .filter(h -> h != null)
                .reduce(0.0, Double::sum);

        timesheet.setTotalMonthlyHours(total);
        timesheetRepository.save(timesheet);
    }
}