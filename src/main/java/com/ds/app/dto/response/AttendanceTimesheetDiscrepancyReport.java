package com.ds.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceTimesheetDiscrepancyReport {
    private Long employeeId;
    private String employeeName;
    private Integer month;
    private Integer year;
    private List<AttendanceTimesheetDiscrepancyRow> rows;
}