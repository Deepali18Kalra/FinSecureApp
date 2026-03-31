package com.ds.app.mapper;

import com.ds.app.dto.TimesheetEntryResponse;
import com.ds.app.dto.TimesheetResponse;
import com.ds.app.entity.Timesheet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TimesheetMapper {

    private final TimesheetEntryMapper timesheetEntryMapper;

    public TimesheetResponse mapToResponse(Timesheet t) {
        List<TimesheetEntryResponse> entries = t.getTimesheetEntries() == null
                ? List.of()
                : t.getTimesheetEntries().stream().map(timesheetEntryMapper::mapToResponse).toList();

        String employeeName = t.getEmployee().getFirstName() + " " + t.getEmployee().getLastName();
        String approvedByName = t.getApprovedBy() == null
                ? null
                : t.getApprovedBy().getFirstName() + " " + t.getApprovedBy().getLastName();

        return TimesheetResponse.builder()
                .timesheetId(t.getTimesheetId())
                .employeeId(t.getEmployee().getUserId())
                .employeeName(employeeName)
                .month(t.getMonth())
                .year(t.getYear())
                .status(t.getStatus())
                .submittedAt(t.getSubmittedAt())
                .approvedByName(approvedByName)
                .approvalDate(t.getApprovalDate())
                .rejectionReason(t.getRejectionReason())
                .totalMonthlyHours(t.getTotalMonthlyHours() == null ? 0 : t.getTotalMonthlyHours().intValue())
                .entries(entries)
                .build();
    }
}