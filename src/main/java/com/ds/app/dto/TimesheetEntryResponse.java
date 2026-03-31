package com.ds.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimesheetEntryResponse {
    private Long timesheetEntryId;
    private Long timesheetId;
    private LocalDate date;
    private String taskDescription;
    private Double hoursWorked;
    private Long projectId;
    private String projectName;
}