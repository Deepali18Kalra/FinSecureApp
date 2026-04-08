package com.ds.app.mapper;

import com.ds.app.dto.response.TimesheetEntryResponse;
import com.ds.app.dto.response.TimesheetResponse;
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

        int totalMins = t.getTotalMonthlyMinutes() != null ? t.getTotalMonthlyMinutes() : 0;

        int hours = totalMins / 60;
        int mins = totalMins % 60;

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
                .totalMonthlyMinutes(totalMins)
                .formattedTotalTime(String.format("%02d:%02d", hours, mins))
                .entries(entries)
                .build();
    }
}