package com.ds.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyAttendanceReport {
    private Long employeeId;
    private String employeeName;
    private Integer month;
    private Integer year;
    private Long presentDays;
    private Long absentDays;
    private Long lateCount;
    private Long halfDayCount;
    private Double totalHoursWorked;
}