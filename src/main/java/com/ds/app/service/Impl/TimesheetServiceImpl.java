package com.ds.app.service.Impl;

import com.ds.app.dto.ApprovalRequest;
import com.ds.app.dto.TimesheetResponse;
import com.ds.app.entity.Employee;
import com.ds.app.entity.Timesheet;
import com.ds.app.enums.ApprovalStatus;
import com.ds.app.enums.TimesheetStatus;
import com.ds.app.exception.ResourceNotFoundException;
import com.ds.app.mapper.TimesheetMapper;
import com.ds.app.repository.ITimesheetRepository;
import com.ds.app.service.ITimesheetService;
import com.ds.app.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TimesheetServiceImpl implements ITimesheetService {

    private final ITimesheetRepository timesheetRepository;
    private final TimesheetMapper timesheetMapper;
    private final SecurityUtils securityUtils;

    @Override
    public TimesheetResponse getMyMonthlyTimesheet(Integer month, Integer year) {
        Employee me = securityUtils.getLoggedInEmployee();

        Timesheet ts = timesheetRepository.findByEmployeeUserIdAndMonthAndYear(me.getUserId(), month, year)
                .orElseThrow(() -> new ResourceNotFoundException("Timesheet not found for month/year"));

        return timesheetMapper.mapToResponse(ts);
    }

    @Override
    @Transactional
    public TimesheetResponse submitMyTimesheet(Long timesheetId) {
        Employee me = securityUtils.getLoggedInEmployee();

        Timesheet ts = timesheetRepository.findByTimesheetIdAndEmployeeUserId(timesheetId, me.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Timesheet not found with id: " + timesheetId));

        if (ts.getStatus() != TimesheetStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT timesheet can be submitted.");
        }

        if (ts.getTotalMonthlyHours() == null || ts.getTotalMonthlyHours() <= 0) {
            throw new IllegalStateException("Cannot submit empty timesheet.");
        }

        ts.setStatus(TimesheetStatus.SUBMITTED);
        ts.setSubmittedAt(LocalDateTime.now());
        ts.setApprovedBy(null);
        ts.setApprovalDate(null);
        ts.setRejectionReason(null);

        return timesheetMapper.mapToResponse(ts);
    }

    @Override
    public Page<TimesheetResponse> getPendingTimesheetsForHr(Pageable pageable) {
        Employee hr = securityUtils.getLoggedInEmployee();
        return timesheetRepository.findByEmployee_Manager_UserIdAndStatus(hr.getUserId(), TimesheetStatus.SUBMITTED, pageable)
                .map(timesheetMapper::mapToResponse);
    }

    @Override
    public Page<TimesheetResponse> getTeamTimesheetsByMonthYear(Integer month, Integer year, Pageable pageable) {
        Employee hr = securityUtils.getLoggedInEmployee();
        return timesheetRepository.findByEmployee_Manager_UserIdAndMonthAndYear(hr.getUserId(), month, year, pageable)
                .map(timesheetMapper::mapToResponse);
    }

    @Override
    @Transactional
    public TimesheetResponse reviewTimesheet(Long timesheetId, ApprovalRequest request) {
        Employee hr = securityUtils.getLoggedInEmployee();

        Timesheet ts = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> new ResourceNotFoundException("Timesheet not found with id: " + timesheetId));

        if (ts.getEmployee().getManager() == null || !ts.getEmployee().getManager().getUserId().equals(hr.getUserId())) {
            throw new ResourceNotFoundException("Timesheet not found with id: " + timesheetId);
        }

        if (ts.getStatus() != TimesheetStatus.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED timesheet can be reviewed.");
        }

        if (request.getStatus() == ApprovalStatus.APPROVED) {
            ts.setStatus(TimesheetStatus.APPROVED);
            ts.setRejectionReason(null);
        } else {
            ts.setStatus(TimesheetStatus.REJECTED);
            ts.setRejectionReason(request.getRejectionReason());
        }

        ts.setApprovedBy(hr);
        ts.setApprovalDate(LocalDate.now());

        return timesheetMapper.mapToResponse(ts);
    }
}