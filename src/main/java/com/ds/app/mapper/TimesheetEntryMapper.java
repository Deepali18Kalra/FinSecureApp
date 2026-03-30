package com.ds.app.mapper;

import org.springframework.stereotype.Component;

import com.ds.app.dto.TimesheetEntryResponse;
import com.ds.app.entity.TimesheetEntry;

@Component
public class TimesheetEntryMapper {

    public TimesheetEntryResponse mapToResponse(TimesheetEntry e) {
        return TimesheetEntryResponse.builder()
                .timesheetEntryId(e.getTimesheetEntryId())
                .timesheetId(e.getTimesheet().getTimesheetId())
                .date(e.getDate())
                .taskDescription(e.getTaskDescription())
                .hoursWorked(e.getHoursWorked())
                .projectId(e.getProjectId())
                .projectName(e.getProjectName())
                .build();
    }
}