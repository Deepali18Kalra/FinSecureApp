package com.ds.app.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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