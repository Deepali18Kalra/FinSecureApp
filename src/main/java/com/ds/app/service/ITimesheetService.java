package com.ds.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ds.app.dto.ApprovalRequest;
import com.ds.app.dto.TimesheetResponse;

public interface ITimesheetService {

    TimesheetResponse getMyMonthlyTimesheet(Integer month, Integer year);

    TimesheetResponse submitMyTimesheet(Long timesheetId);

    Page<TimesheetResponse> getPendingTimesheetsForHr(Pageable pageable);

    Page<TimesheetResponse> getTeamTimesheetsByMonthYear(Integer month, Integer year, Pageable pageable);

    TimesheetResponse reviewTimesheet(Long timesheetId, ApprovalRequest request);
}