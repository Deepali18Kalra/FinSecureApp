package com.ds.app.mapper;

import com.ds.app.dto.request.TimesheetEntryRequest;
import com.ds.app.dto.response.TimesheetEntryResponse;
import com.ds.app.entity.Timesheet;
import com.ds.app.entity.TimesheetEntry;
import org.springframework.stereotype.Component;

@Component
public class TimesheetEntryMapper {

    // inbound mapping (DTO -> Entity)
    public TimesheetEntry mapToEntity(TimesheetEntryRequest request, Timesheet timesheet) {

        int totalMinutes = (request.getHours() * 60) + request.getMinutes();

        return TimesheetEntry.builder()
                .timesheet(timesheet)
                .date(request.getDate())
                .taskDescription(request.getTaskDescription())
                .totalMinutesWorked(totalMinutes)
                .projectId(request.getProjectId())
                .projectName(request.getProjectName())
                .build();
    }

    // outbound mapping (Entity -> DTO)
    public TimesheetEntryResponse mapToResponse(TimesheetEntry e) {

        // Convert the clean integer back into hours and minutes for the UI
        int hours = e.getTotalMinutesWorked() / 60;
        int minutes = e.getTotalMinutesWorked() % 60;

        return TimesheetEntryResponse.builder()
                .timesheetEntryId(e.getTimesheetEntryId())
                .timesheetId(e.getTimesheet().getTimesheetId())
                .date(e.getDate())
                .taskDescription(e.getTaskDescription())
                .totalMinutesWorked(e.getTotalMinutesWorked()) // Send the raw integer
                .formattedTime(String.format("%02d:%02d", hours, minutes)) // Send the pretty string
                .projectId(e.getProjectId())
                .projectName(e.getProjectName())
                .build();
    }
}