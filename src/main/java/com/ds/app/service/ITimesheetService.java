package com.ds.app.service;

import com.ds.app.dto.request.ApprovalRequest;
import com.ds.app.dto.response.AttendanceTimesheetDiscrepancyReport;
import com.ds.app.dto.response.TimesheetResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITimesheetService {

    TimesheetResponse getMyMonthlyTimesheet(Integer month, Integer year);

    TimesheetResponse submitMyTimesheet(Long timesheetId);

    Page<TimesheetResponse> getPendingTimesheetsForManager(Pageable pageable);

    Page<TimesheetResponse> getTeamTimesheetsByMonthYear(Integer month, Integer year, Pageable pageable);

    TimesheetResponse reviewTimesheet(Long timesheetId, ApprovalRequest request);

    AttendanceTimesheetDiscrepancyReport getAttendanceTimesheetDiscrepancyReport(Long employeeId, Integer month, Integer year);
}