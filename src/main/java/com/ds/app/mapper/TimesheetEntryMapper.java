package com.ds.app.mapper;

import com.ds.app.dto.TimesheetEntryResponse;
import com.ds.app.entity.TimesheetEntry;
import org.springframework.stereotype.Component;

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