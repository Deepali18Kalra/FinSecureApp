package com.ds.app.dto.response;

import com.ds.app.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceTimesheetDiscrepancyRow {
    private LocalDate date;
    private AttendanceStatus attendanceStatus;
    private Double attendanceHours;
    private Double timesheetHours;
    private String discrepancy; // OK, MISMATCH, WARNING
}