package com.ds.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

import com.ds.app.enums.AttendanceStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {
    private Long attendanceId;
    private Long employeeId;
    private String employeeName;
    private LocalDate date;
    private LocalTime punchInTime;
    private LocalTime punchOutTime;
    private AttendanceStatus status;
    private Double hoursWorked;
    private Boolean isRegularized;
}